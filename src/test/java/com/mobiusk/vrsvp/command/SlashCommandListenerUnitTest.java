package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SlashCommandListenerUnitTest extends TestBase {

	@InjectMocks private SlashCommandListener listener;

	@Mock private SlashCommandReply reply;

	private final int SLASH_COMMAND_INPUT_COUNT = SlashCommandEnum.values().length;

	@BeforeEach
	public void beforeEach() {
		when(slashCommandInteractionEvent.getUser()).thenReturn(user);
	}

	@Test
	void unexpectedSlashCommandsAreIgnored() {

		when(slashCommandInteractionEvent.getName()).thenReturn("unknown-command");

		listener.onSlashCommandInteraction(slashCommandInteractionEvent);

		verify(slashCommandInteractionEvent, times(2)).getName();
		verify(reply, never()).rsvpCreation(any(), any());
	}

	@Test
	void slashCommandDefaultsInputsToInvalidValuesIfNotFound() {

		IntStream.range(0, SLASH_COMMAND_INPUT_COUNT).forEach(ignored -> when(slashCommandInteractionEvent.getOption(any())).thenReturn(null));

		when(slashCommandInteractionEvent.getName()).thenReturn(SlashCommandUi.INVOCATION);

		listener.onSlashCommandInteraction(slashCommandInteractionEvent);

		verify(reply).rsvpCreation(eq(slashCommandInteractionEvent), inputsArgumentCaptor.capture());

		var inputs = inputsArgumentCaptor.getValue();
		assertNull(inputs.getBlocks());
		assertNull(inputs.getSlots());
		assertNull(inputs.getDurationInMinutes());
		assertNull(inputs.getStartTimestamp());
		assertNull(inputs.getRsvpLimitPerPerson());
		assertNull(inputs.getRsvpLimitPerSlot());
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

		when(slashCommandInteractionEvent.getOption(SlashCommandEnum.BLOCKS.getId())).thenReturn(options.get(0));
		when(slashCommandInteractionEvent.getOption(SlashCommandEnum.SLOTS.getId())).thenReturn(options.get(1));
		when(slashCommandInteractionEvent.getOption(SlashCommandEnum.DURATION.getId())).thenReturn(options.get(2));
		when(slashCommandInteractionEvent.getOption(SlashCommandEnum.START.getId())).thenReturn(options.get(3));

		when(slashCommandInteractionEvent.getName()).thenReturn(SlashCommandUi.INVOCATION);

		listener.onSlashCommandInteraction(slashCommandInteractionEvent);

		verify(reply).rsvpCreation(eq(slashCommandInteractionEvent), inputsArgumentCaptor.capture());

		var inputs = inputsArgumentCaptor.getValue();
		assertEquals(1, inputs.getBlocks());
		assertEquals(2, inputs.getSlots());
		assertEquals(3, inputs.getDurationInMinutes());
		assertEquals(4, inputs.getStartTimestamp());
		assertNull(inputs.getRsvpLimitPerPerson());
		assertNull(inputs.getRsvpLimitPerSlot());
	}

}
