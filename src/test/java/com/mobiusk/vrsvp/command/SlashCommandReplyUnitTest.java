package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.button.ButtonUi;
import com.mobiusk.vrsvp.embed.EmbedUi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SlashCommandReplyUnitTest extends TestBase {

	@InjectMocks private SlashCommandReply reply;

	@Mock private ButtonUi buttonUi;
	@Mock private EmbedUi embedUi;

	private final SlashCommandInputs inputs = new SlashCommandInputs();

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

		inputs.setBlocks(5);
		inputs.setSlots(10);

		reply.rsvpCreation(slashCommandInteractionEvent, inputs);

		verify(slashCommandInteractionEvent).reply(stringArgumentCaptor.capture());
		verifyRsvpCreationActions(times(1), never());

		var expectation = "The maximum amount of (blocks * slots) allowed in VRSVP is 25 due to a Discord limitation. Please retry this command with a smaller total block/slot count, or split your RSVP into more than one form.";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void rsvpCreationReturnsNonEphemeralFormReplyIfValidationPasses() {

		reply.rsvpCreation(slashCommandInteractionEvent, inputs);

		verify(slashCommandInteractionEvent).reply(stringArgumentCaptor.capture());
		verifyRsvpCreationActions(never(), times(1));

		var expectation = "---\n**Signups are now available for a new event**\n\nSlots start <t:5:R> on <t:5:F> and each is 4 minute(s) long.\n---";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	// Test utility methods

	private void verifyRsvpCreationActions(VerificationMode ephemeralExpectation, VerificationMode buildExpectation) {

		verify(replyCallbackAction, ephemeralExpectation).setEphemeral(true);
		verify(embedUi, buildExpectation).build(inputs);
		verify(buttonUi, buildExpectation).buildRsvpActionPrompts();
		verify(replyCallbackAction, buildExpectation).addEmbeds(anyCollection());
		verify(replyCallbackAction, buildExpectation).addActionRow(anyCollection());

		verify(replyCallbackAction).queue();
	}

}
