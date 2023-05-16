package com.mobiusk.vrsvp;

import com.mobiusk.vrsvp.input.Inputs;
import com.mobiusk.vrsvp.input.InputsEnum;
import com.mobiusk.vrsvp.output.OutputsAutoComplete;
import com.mobiusk.vrsvp.output.OutputsReplies;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

class EventListenerTest extends TestBase {

	@InjectMocks
	private EventListener eventListener;

	@Mock
	private OutputsAutoComplete outputsAutoComplete;

	@Mock
	private OutputsReplies outputsReplies;

	@Mock
	private CommandAutoCompleteInteractionEvent commandAutoCompleteEvent;

	@Mock
	private SlashCommandInteractionEvent slashCommandEvent;

	@Captor
	private ArgumentCaptor<Inputs> inputsArgumentCaptor;

	private final int SLASH_COMMAND_INPUT_COUNT = InputsEnum.values().length;

	@Test
	void commandAutoCompleteEventsIntercepted() {

		var autoCompleteQuery = mock(AutoCompleteQuery.class);
		when(autoCompleteQuery.getName()).thenReturn("Testing");
		when(commandAutoCompleteEvent.getFocusedOption()).thenReturn(autoCompleteQuery);

		eventListener.onCommandAutoCompleteInteraction(commandAutoCompleteEvent);

		verify(outputsAutoComplete).reply(commandAutoCompleteEvent, "Testing");
	}

	@Test
	void unexpectedSlashCommandsAreIgnored() {

		when(slashCommandEvent.getName()).thenReturn("unknown-command");

		eventListener.onSlashCommandInteraction(slashCommandEvent);

		verify(slashCommandEvent).getName();
		verify(outputsReplies, never()).rsvpCreation(any(), any());
	}

	@Test
	void slashCommandDefaultsInputsToInvalidValuesIfNotFound() {

		IntStream.range(0, SLASH_COMMAND_INPUT_COUNT).forEach(ignored -> when(slashCommandEvent.getOption(any())).thenReturn(null));

		when(slashCommandEvent.getName()).thenReturn(Commands.SLASH);

		eventListener.onSlashCommandInteraction(slashCommandEvent);

		verify(outputsReplies).rsvpCreation(eq(slashCommandEvent), inputsArgumentCaptor.capture());

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

		when(slashCommandEvent.getOption(InputsEnum.BLOCKS.getInput())).thenReturn(options.get(0));
		when(slashCommandEvent.getOption(InputsEnum.SLOTS.getInput())).thenReturn(options.get(1));
		when(slashCommandEvent.getOption(InputsEnum.DURATION.getInput())).thenReturn(options.get(2));
		when(slashCommandEvent.getOption(InputsEnum.START.getInput())).thenReturn(options.get(3));

		when(slashCommandEvent.getName()).thenReturn(Commands.SLASH);

		eventListener.onSlashCommandInteraction(slashCommandEvent);

		verify(outputsReplies).rsvpCreation(eq(slashCommandEvent), inputsArgumentCaptor.capture());

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
