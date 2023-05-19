package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.input.Inputs;
import com.mobiusk.vrsvp.input.InputsEnum;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SlashCommandListenerUnitTest extends TestBase {

	@InjectMocks private SlashCommandListener listener;

	@Mock private SlashCommandReply reply;

	private final int SLASH_COMMAND_INPUT_COUNT = InputsEnum.values().length;

	@Test
	void unexpectedSlashCommandsAreIgnored() {

		when(slashCommandInteractionEvent.getName()).thenReturn("unknown-command");

		listener.onSlashCommandInteraction(slashCommandInteractionEvent);

		verify(slashCommandInteractionEvent).getName();
		verify(reply, never()).rsvpCreation(any(), any());
	}

	@Test
	void slashCommandDefaultsInputsToInvalidValuesIfNotFound() {

		IntStream.range(0, SLASH_COMMAND_INPUT_COUNT).forEach(ignored -> when(slashCommandInteractionEvent.getOption(any())).thenReturn(null));

		when(slashCommandInteractionEvent.getName()).thenReturn(SlashCommandUi.INVOCATION);

		listener.onSlashCommandInteraction(slashCommandInteractionEvent);

		verify(reply).rsvpCreation(eq(slashCommandInteractionEvent), inputsArgumentCaptor.capture());

		var inputs = inputsArgumentCaptor.getValue();
		assertInputValues(inputs, -1, -1, -1, -1);
	}

	@Test
	void slashCommandEventsIntercepted() {

		// Mockito does not allow us to inline these mock creations inside .thenReturn()
		var options = IntStream
			.range(1, SLASH_COMMAND_INPUT_COUNT + 1)
			.mapToObj(value -> {
				var optionMapping = mock(OptionMapping.class);
				when(optionMapping.getAsInt()).thenReturn(value);
				return optionMapping;
			})
			.toList();

		when(slashCommandInteractionEvent.getOption(InputsEnum.BLOCKS.getId())).thenReturn(options.get(0));
		when(slashCommandInteractionEvent.getOption(InputsEnum.SLOTS.getId())).thenReturn(options.get(1));
		when(slashCommandInteractionEvent.getOption(InputsEnum.DURATION.getId())).thenReturn(options.get(2));
		when(slashCommandInteractionEvent.getOption(InputsEnum.START.getId())).thenReturn(options.get(3));

		when(slashCommandInteractionEvent.getName()).thenReturn(SlashCommandUi.INVOCATION);

		listener.onSlashCommandInteraction(slashCommandInteractionEvent);

		verify(reply).rsvpCreation(eq(slashCommandInteractionEvent), inputsArgumentCaptor.capture());

		var inputs = inputsArgumentCaptor.getValue();
		assertInputValues(inputs, 1, 2, 3, 4);
	}

	// Test utility method(s)

	private void assertInputValues(Inputs inputs, int blocks, int slots, int durationInMinutes, int startTimestamp) {
		assertEquals(blocks, inputs.getBlocks());
		assertEquals(slots, inputs.getSlots());
		assertEquals(durationInMinutes, inputs.getDurationInMinutes());
		assertEquals(startTimestamp, inputs.getStartTimestamp());
	}

}
