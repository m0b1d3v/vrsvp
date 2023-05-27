package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.modal.ModalEnum;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.internal.interactions.component.TextInputImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SlashCommandReplyUnitTest extends TestBase {

	@InjectMocks private SlashCommandReply reply;

	private final SlashCommandInputs inputs = new SlashCommandInputs();

	@BeforeEach
	public void beforeEach() {
		when(slashCommandInteractionEvent.replyModal(any())).thenReturn(modalCallbackAction);
	}

	@Test
	void rsvpCreationReturnsModalToCheckInputs() {

		inputs.setStartTimestamp(1);
		inputs.setSlots(2);
		inputs.setDurationInMinutes(20);

		reply.rsvpCreation(slashCommandInteractionEvent, inputs);

		verify(slashCommandInteractionEvent).replyModal(modalArgumentCaptor.capture());
		verify(modalCallbackAction).queue();

		var modal = modalArgumentCaptor.getValue();

		var textInput = (TextInputImpl) modal.getComponents().get(0).getActionComponents().get(0);
		assertEquals(ModalEnum.EVENT_CREATION.getId(), textInput.getId());
		assertEquals(ModalEnum.EVENT_CREATION.getLabel(), textInput.getLabel());
		assertEquals(ModalEnum.EVENT_CREATION.getPlaceholder(), textInput.getPlaceHolder());
		assertEquals(TextInputStyle.PARAGRAPH, textInput.getStyle());
	}

	@Test
	void rsvpCreationWithoutRsvpLimitAddendum() {

		var inputs = new SlashCommandInputs();
		inputs.setSlots(2);
		inputs.setDurationInMinutes(5);
		inputs.setStartTimestamp(10);

		reply.rsvpCreation(slashCommandInteractionEvent, inputs);

		verify(slashCommandInteractionEvent).replyModal(modalArgumentCaptor.capture());

		var modal = modalArgumentCaptor.getValue();
		var textInput = (TextInputImpl) modal.getComponents().get(0).getActionComponents().get(0);

		var expectation = """
			**New Event**
			
			- Starts <t:10:R> on <t:10:F>
			- Each slot is 5 minutes long
			
			> #1, <t:10:t>
			> #2, <t:310:t>""";

		assertEquals(expectation, textInput.getValue());
	}

	@Test
	void rsvpCreationWithRsvpLimitAddendum() {

		var inputs = new SlashCommandInputs();
		inputs.setSlots(3);
		inputs.setDurationInMinutes(20);
		inputs.setStartTimestamp(100);
		inputs.setRsvpLimitPerPerson(3);
		inputs.setRsvpLimitPerSlot(2);

		reply.rsvpCreation(slashCommandInteractionEvent, inputs);

		verify(slashCommandInteractionEvent).replyModal(modalArgumentCaptor.capture());

		var modal = modalArgumentCaptor.getValue();
		var textInput = (TextInputImpl) modal.getComponents().get(0).getActionComponents().get(0);

		var expectation = """
			**New Event**
			
			- Starts <t:100:R> on <t:100:F>
			- Each slot is 20 minutes long
			- Maximum number of people that can RSVP for a single slot: 2
			- Maximum number of slots a person can RSVP for: 3
			
			> #1, <t:100:t>
			> #2, <t:1300:t>
			> #3, <t:2500:t>""";

		assertEquals(expectation, textInput.getValue());
	}

}
