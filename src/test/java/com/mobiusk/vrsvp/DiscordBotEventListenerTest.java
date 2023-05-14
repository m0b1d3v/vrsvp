package com.mobiusk.vrsvp;

import com.mobiusk.vrsvp.input.DiscordBotInputsEnum;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiscordBotEventListenerTest extends TestBase {

	@InjectMocks
	private DiscordBotEventListener eventListener;

	@Mock
	private CommandAutoCompleteInteractionEvent commandAutoCompleteEvent;

	@Mock
	private SlashCommandInteractionEvent slashCommandEvent;

	@Mock
	private AutoCompleteQuery autoCompleteQuery;

	@Mock
	private AutoCompleteCallbackAction autoCompleteCallbackAction;

	@Mock
	private ReplyCallbackAction replyCallbackAction;

	@Captor
	private ArgumentCaptor<List<Long>> listLongArgumentCaptor;

	@Captor
	private ArgumentCaptor<String> stringArgumentCaptor;

	private final int SLASH_COMMAND_INPUT_COUNT = DiscordBotInputsEnum.values().length;

	@BeforeEach
	public void beforeEach() {

		when(commandAutoCompleteEvent.getFocusedOption()).thenReturn(autoCompleteQuery);
		when(commandAutoCompleteEvent.replyChoiceLongs(anyCollection())).thenReturn(autoCompleteCallbackAction);

		when(replyCallbackAction.setEphemeral(anyBoolean())).thenReturn(replyCallbackAction);

		when(slashCommandEvent.reply(any(String.class))).thenReturn(replyCallbackAction);
	}

	// Command autoComplete tests

	@Test
	void unexpectedCommandAutoCompleteEventsAreIgnored() {

		var fieldNames = List.of("unknown-field", DiscordBotInputsEnum.START.getInput());

		for (var fieldName : fieldNames) {
			runCommandAutoComplete(fieldName);
		}

		verify(autoCompleteQuery, times(fieldNames.size())).getName();
		verify(commandAutoCompleteEvent, never()).replyChoiceLongs(anyCollection());
		verify(autoCompleteCallbackAction, never()).queue();
	}

	@Test
	void commandAutoCompleteOptionsBlocks() {
		assertCommandAutoCompleteOptions(DiscordBotInputsEnum.BLOCKS);
	}

	@Test
	void commandAutoCompleteOptionsDuration() {
		assertCommandAutoCompleteOptions(DiscordBotInputsEnum.DURATION);
	}

	@Test
	void commandAutoCompleteOptionsSlots() {
		assertCommandAutoCompleteOptions(DiscordBotInputsEnum.SLOTS);
	}

	// Slash command tests

	@Test
	void unexpectedSlashCommandsAreIgnored() {

		when(slashCommandEvent.getName()).thenReturn("unknown-command");

		eventListener.onSlashCommandInteraction(slashCommandEvent);

		verify(slashCommandEvent).getName();
		verify(slashCommandEvent, never()).reply(any(String.class));
	}

	@Test
	void slashCommandReturnsEphemeralReply() {

		runSlashCommand();

		verify(slashCommandEvent).reply(any(String.class));
		verify(replyCallbackAction).setEphemeral(true);
		verify(replyCallbackAction).queue();
	}

	@Test
	void slashCommandDefaultsInputsToInvalidValuesIfNotFound() {

		IntStream.range(0, SLASH_COMMAND_INPUT_COUNT).forEach(ignored -> when(slashCommandEvent.getOption(any())).thenReturn(null));

		runSlashCommand();

		verify(slashCommandEvent).reply(stringArgumentCaptor.capture());

		var expectation = """
			The minimum amount of blocks required in VRSVP is 1, otherwise there is nothing to RSVP for. Please retry this command with a larger block count.
			The minimum duration in minutes for each slot in VRSVP is one minute. Please retry this command with a larger duration.
			The minimum amount of slots required in VRSVP is 1, otherwise there is nothing to RSVP for. Please retry this command with a larger slots count.
			The minimum start timestamp in VRSVP is 0, which equates to 1970-01-01. This is to ensure compatibility with Discord timestamp formatting. Please retry this command with a larger start timestamp.""";

		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void slashCommandUsesInputsToGenerateReply() {

		// Mockito does not allow us to inline these mock creations inside .thenReturn()
		var options = IntStream.range(1, SLASH_COMMAND_INPUT_COUNT + 1).mapToObj(this::mockOptionMapping).toList();

		when(slashCommandEvent.getOption(DiscordBotInputsEnum.BLOCKS.getInput())).thenReturn(options.get(0));
		when(slashCommandEvent.getOption(DiscordBotInputsEnum.SLOTS.getInput())).thenReturn(options.get(1));
		when(slashCommandEvent.getOption(DiscordBotInputsEnum.DURATION.getInput())).thenReturn(options.get(2));
		when(slashCommandEvent.getOption(DiscordBotInputsEnum.START.getInput())).thenReturn(options.get(3));

		runSlashCommand();

		verify(slashCommandEvent).reply(stringArgumentCaptor.capture());

		assertSlashCommandReplyExpectation(stringArgumentCaptor.getValue(), 1, 2, 3, 4);
	}

	// Test utility method(s)

	private void runCommandAutoComplete(String fieldName) {
		when(autoCompleteQuery.getName()).thenReturn(fieldName);
		eventListener.onCommandAutoCompleteInteraction(commandAutoCompleteEvent);
	}

	private void runSlashCommand() {
		when(slashCommandEvent.getName()).thenReturn(DiscordBotCommands.SLASH);
		eventListener.onSlashCommandInteraction(slashCommandEvent);
	}

	private OptionMapping mockOptionMapping(int value) {
		var optionMapping = mock(OptionMapping.class);
		when(optionMapping.getAsInt()).thenReturn(value);
		return optionMapping;
	}

	private void assertCommandAutoCompleteOptions(DiscordBotInputsEnum inputsEnum) {

		var input = inputsEnum.getInput();

		runCommandAutoComplete(input);

		verify(commandAutoCompleteEvent).replyChoiceLongs(listLongArgumentCaptor.capture());
		verify(autoCompleteCallbackAction).queue();

		var choices = listLongArgumentCaptor.getValue();
		assertEquals(DiscordBotCommands.INPUT_AUTOCOMPLETE_OPTIONS.get(input), choices);
	}

	private void assertSlashCommandReplyExpectation(String reply, int blocks, int slots, int duration, int start) {
		assertEquals(
			String.format("Will build RSVP form with %d blocks, %d slots each, %d minutes per slot, starting at <t:%d:F>", blocks, slots, duration, start),
			reply
		);
	}

}
