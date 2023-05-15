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
import static org.mockito.ArgumentMatchers.*;
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
	void slashCommandReturnsEphemeralReply() {

		output.reply(event, inputs);

		verify(event).reply(any(String.class));
		verify(callback).setEphemeral(true);
		verify(callback).queue();
	}

	@Test
	void slashCommandReturnsValidationMessageIfApplicable() {

		inputs.setDurationInMinutes(-1);

		output.reply(event, inputs);

		verify(event).reply(stringArgumentCaptor.capture());

		var expectation = "The minimum duration in minutes for each slot in VRSVP is one minute. Please retry this command with a larger duration.";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void slashCommandUsesInputsToGenerateReply() {

		output.reply(event, inputs);

		verify(event).reply(stringArgumentCaptor.capture());

		var expectation = "Will build RSVP form with 1 blocks, 2 slots each, 3 minutes per slot, starting at <t:4:F>";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

}
