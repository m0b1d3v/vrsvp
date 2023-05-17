package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.input.Inputs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutputsRepliesUnitTest extends TestBase {

	@InjectMocks
	private OutputsReplies output;

	@Mock
	private OutputsButtons outputsButtons;

	@Mock
	private OutputsEmbeds outputsEmbeds;

	@Mock
	private ButtonInteraction buttonInteraction;

	@Mock
	private ButtonInteractionEvent buttonInteractionEvent;

	@Mock
	private SlashCommandInteractionEvent slashCommandInteractionEvent;

	@Mock
	private MessageEditCallbackAction messageEditCallbackAction;

	@Mock
	private ReplyCallbackAction replyCallbackAction;

	@Mock
	private Message message;

	@Captor
	private ArgumentCaptor<String> stringArgumentCaptor;

	private final Inputs inputs = new Inputs();

	@BeforeEach
	public void beforeEach() {

		when(replyCallbackAction.setEphemeral(anyBoolean())).thenReturn(replyCallbackAction);
		when(replyCallbackAction.addEmbeds(anyCollection())).thenReturn(replyCallbackAction);
		when(replyCallbackAction.addActionRow(anyCollection())).thenReturn(replyCallbackAction);

		when(buttonInteraction.editMessage(anyString())).thenReturn(messageEditCallbackAction);

		when(buttonInteractionEvent.getInteraction()).thenReturn(buttonInteraction);
		when(buttonInteractionEvent.reply(anyString())).thenReturn(replyCallbackAction);

		when(slashCommandInteractionEvent.reply(anyString())).thenReturn(replyCallbackAction);

		inputs.setBlocks(2);
		inputs.setSlots(3);
		inputs.setDurationInMinutes(4);
		inputs.setStartTimestamp(5);
	}

	@Test
	void rsvpCreationReturnsEphemeralValidationMessageIfValidationFails() {

		inputs.setDurationInMinutes(-1);

		output.rsvpCreation(slashCommandInteractionEvent, inputs);

		verify(slashCommandInteractionEvent).reply(stringArgumentCaptor.capture());
		verifyRsvpCreationActions(times(1), never());

		var expectation = "The minimum duration in minutes for each slot in VRSVP is one minute. Please retry this command with a larger duration.";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void rsvpCreationReturnsNonEphemeralFormReplyIfValidationPasses() {

		output.rsvpCreation(slashCommandInteractionEvent, inputs);

		verify(slashCommandInteractionEvent).reply(stringArgumentCaptor.capture());
		verifyRsvpCreationActions(never(), times(1));

		var expectation = "---\n**Signups are now available for a new event**\n\nSlots start <t:5:R> on <t:5:F> and each is 4 minute(s) long.\n---";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void rsvpInterestBuildsEphemeralListOfButtons() {

		var totalSlots = inputs.getBlocks() * inputs.getSlots();
		List<List<Button>> buttonRows = List.of(Collections.emptyList(), Collections.emptyList());
		when(outputsButtons.buildSlotSignupActionRows(totalSlots)).thenReturn(buttonRows);

		output.rsvpInterest(buttonInteractionEvent, totalSlots);

		verify(buttonInteractionEvent).reply(stringArgumentCaptor.capture());
		verify(outputsButtons).buildSlotSignupActionRows(totalSlots);
		verify(replyCallbackAction).setEphemeral(true);
		verify(replyCallbackAction, times(buttonRows.size())).addActionRow(anyCollection());
		verify(replyCallbackAction).queue();

		var expectation = "---\nUse these buttons to toggle your RSVP for any slot.\n---";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void rsvpToggleAdjustsEmbedAndEditsEphemeralMessage() {

		var userMention = "@Testing";
		var slotIndex = 1;

		output.rsvpToggle(buttonInteractionEvent, message, userMention, slotIndex);

		verify(buttonInteractionEvent).getInteraction();
		verify(buttonInteraction).editMessage(stringArgumentCaptor.capture());
		verify(messageEditCallbackAction).queue();

		var expectation = "---\nRSVP state toggled for slot #2\n---";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	// Test utility methods

	private void verifyRsvpCreationActions(VerificationMode ephemeralExpectation, VerificationMode buildExpectation) {

		verify(replyCallbackAction, ephemeralExpectation).setEphemeral(true);
		verify(outputsEmbeds, buildExpectation).build(inputs);
		verify(outputsButtons, buildExpectation).buildRsvpActionPrompts();
		verify(replyCallbackAction, buildExpectation).addEmbeds(anyCollection());
		verify(replyCallbackAction, buildExpectation).addActionRow(anyCollection());

		verify(replyCallbackAction).queue();
	}

}
