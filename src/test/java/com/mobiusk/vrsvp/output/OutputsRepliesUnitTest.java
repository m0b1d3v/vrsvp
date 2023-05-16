package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.input.Inputs;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;

import java.util.Objects;

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
	private SlashCommandInteractionEvent event;

	@Mock
	private ReplyCallbackAction callback;

	@Captor
	private ArgumentCaptor<String> stringArgumentCaptor;

	private final Inputs inputs = new Inputs();

	@BeforeEach
	public void beforeEach() {

		when(callback.setEphemeral(anyBoolean())).thenReturn(callback);
		when(callback.addEmbeds(anyCollection())).thenReturn(callback);
		when(callback.addActionRow(anyCollection())).thenReturn(callback);

		when(event.reply(anyString())).thenReturn(callback);

		inputs.setBlocks(2);
		inputs.setSlots(3);
		inputs.setDurationInMinutes(4);
		inputs.setStartTimestamp(5);
	}

	@Test
	void slashCommandReturnsEphemeralValidationMessageIfValidationFails() {

		inputs.setDurationInMinutes(-1);

		output.rsvpCreation(event, inputs);

		verify(event).reply(stringArgumentCaptor.capture());
		verifySlashCommandActions(times(1), never());

		var expectation = "The minimum duration in minutes for each slot in VRSVP is one minute. Please retry this command with a larger duration.";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void slashCommandReturnsNonEphemeralFormReplyIfValidationPasses() {

		output.rsvpCreation(event, inputs);

		verify(event).reply(stringArgumentCaptor.capture());
		verifySlashCommandActions(never(), times(1));

		var expectation = "---\n**Signups are now available for a new event**\n\nSlots start <t:5:R> on <t:5:F> and each is 4 minute(s) long.\n---";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	// Test utility methods

	private void verifySlashCommandActions(VerificationMode ephemeralExpectation, VerificationMode buildExpectation) {

		verify(callback, ephemeralExpectation).setEphemeral(true);
		verify(outputsEmbeds, buildExpectation).build(inputs);
		verify(outputsButtons, buildExpectation).buildRsvpActionPrompts();
		verify(callback, buildExpectation).addEmbeds(anyCollection());
		verify(callback, buildExpectation).addActionRow(anyCollection());

		verify(callback).queue();
	}

}
