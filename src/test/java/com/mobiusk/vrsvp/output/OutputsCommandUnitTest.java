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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutputsCommandUnitTest extends TestBase {

	@InjectMocks
	private OutputsCommand output;

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

		when(event.reply(anyString())).thenReturn(callback);

		inputs.setBlocks(1);
		inputs.setSlots(2);
		inputs.setDurationInMinutes(3);
		inputs.setStartTimestamp(4);
	}

	@Test
	void slashCommandReturnsEphemeralValidationMessageIfValidationFails() {

		inputs.setDurationInMinutes(-1);

		output.reply(event, inputs);

		verify(event).reply(stringArgumentCaptor.capture());
		verify(callback).setEphemeral(true);
		verify(callback).queue();

		var expectation = "The minimum duration in minutes for each slot in VRSVP is one minute. Please retry this command with a larger duration.";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void slashCommandReturnsNonEphemeralFormReplyIfValidationPasses() {

		output.reply(event, inputs);

		verify(event).reply(stringArgumentCaptor.capture());
		verify(callback, never()).setEphemeral(true);
		verify(callback).queue();

		var expectation = "---\n**Signups are now available for a new event**\n\nSlots start <t:4:R> on <t:4:F> and each is 3 minute(s) long.\n---";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

}
