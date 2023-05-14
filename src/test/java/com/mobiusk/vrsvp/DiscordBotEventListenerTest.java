package com.mobiusk.vrsvp;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
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
	private AutoCompleteQuery autoCompleteQuery;

	@Mock
	private AutoCompleteCallbackAction autoCompleteCallbackAction;

	@Captor
	private ArgumentCaptor<List<Long>> listLongArgumentCaptor;

	@BeforeEach
	public void beforeEach() {

		when(commandAutoCompleteEvent.getFocusedOption()).thenReturn(autoCompleteQuery);
		when(commandAutoCompleteEvent.replyChoiceLongs(anyCollection())).thenReturn(autoCompleteCallbackAction);
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

	// Test utility method(s)

	private void runCommandAutoComplete(String fieldName) {
		when(autoCompleteQuery.getName()).thenReturn(fieldName);
		eventListener.onCommandAutoCompleteInteraction(commandAutoCompleteEvent);
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

}
