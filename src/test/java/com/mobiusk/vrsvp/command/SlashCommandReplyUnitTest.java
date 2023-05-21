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

		inputs.setSlots(3);
		inputs.setDurationInMinutes(4);
		inputs.setStartTimestamp(5);
	}

	@Test
	void rsvpCreationReturnsNonEphemeralFormReplyIfValidationPasses() {

		reply.rsvpCreation(slashCommandInteractionEvent, inputs);

		verify(slashCommandInteractionEvent).reply(stringArgumentCaptor.capture());
		verifyRsvpCreationActions(never(), times(1));

		var expectation = "---\n**New Event**\n\n- Starts <t:5:R> on <t:5:F>\n- Each slot is 4 minute(s) long.\n---";
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
