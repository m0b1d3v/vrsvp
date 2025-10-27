package dev.m0b1.vrsvp.command;

import dev.m0b1.vrsvp.TestBase;
import dev.m0b1.vrsvp.modal.ModalEnum;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SlashCommandReplyUnitTest extends TestBase {

	@InjectMocks private SlashCommandReply reply;

	private SlashCommandInputs inputs;

	@BeforeEach
	void beforeEach() {

		inputs = new SlashCommandInputs();

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

		var label = getLabelComponentFromModal();
		var textInput = getTextInputComponentFromModal();
		Assertions.assertEquals(ModalEnum.EVENT_CREATION.getId(), textInput.getCustomId());
		assertEquals(ModalEnum.EVENT_CREATION.getLabel(), label.getLabel());
		assertEquals(ModalEnum.EVENT_CREATION.getPlaceholder(), textInput.getPlaceHolder());
		assertEquals(TextInputStyle.PARAGRAPH, textInput.getStyle());
	}

	@Test
	void rsvpCreationWithoutRsvpLimitAddendum() {

		inputs.setSlots(2);
		inputs.setDurationInMinutes(5);
		inputs.setStartTimestamp(10);

		reply.rsvpCreation(slashCommandInteractionEvent, inputs);

		verify(slashCommandInteractionEvent).replyModal(modalArgumentCaptor.capture());

		var textInput = getTextInputComponentFromModal();

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

		inputs.setSlots(3);
		inputs.setDurationInMinutes(20);
		inputs.setStartTimestamp(100);
		inputs.setRsvpLimitPerPerson(3);
		inputs.setRsvpLimitPerSlot(2);

		reply.rsvpCreation(slashCommandInteractionEvent, inputs);

		verify(slashCommandInteractionEvent).replyModal(modalArgumentCaptor.capture());

		var textInput = getTextInputComponentFromModal();

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

	private Label getLabelComponentFromModal() {
		return modalArgumentCaptor
			.getValue()
			.getComponents()
			.getFirst()
			.asLabel();
	}

	private TextInput getTextInputComponentFromModal() {
		return getLabelComponentFromModal()
			.getChild()
			.asTextInput();
	}

}
