package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.Commands;
import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.input.InputsEnum;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutputsAutoCompleteUnitTest extends TestBase {

	@InjectMocks
	private OutputsAutoComplete output;

	@Mock
	private CommandAutoCompleteInteractionEvent event;

	@Mock
	private AutoCompleteCallbackAction callback;

	@Captor
	private ArgumentCaptor<List<Long>> listLongArgumentCaptor;

	@BeforeEach
	public void beforeEach() {
		when(event.replyChoiceLongs(anyCollection())).thenReturn(callback);
	}

	@Test
	void unexpectedCommandAutoCompleteEventsAreIgnored() {

		var fieldNames = List.of("unknown-field", InputsEnum.START.getInput());

		for (var fieldName : fieldNames) {
			output.reply(event, fieldName);
		}

		verify(event, never()).replyChoiceLongs(anyCollection());
		verify(callback, never()).queue();
	}

	@Test
	void commandAutoCompleteOptionsBlocks() {
		assertCommandAutoCompleteOptions(InputsEnum.BLOCKS);
	}

	@Test
	void commandAutoCompleteOptionsDuration() {
		assertCommandAutoCompleteOptions(InputsEnum.DURATION);
	}

	@Test
	void commandAutoCompleteOptionsSlots() {
		assertCommandAutoCompleteOptions(InputsEnum.SLOTS);
	}

	// Test utility methods

	private void assertCommandAutoCompleteOptions(InputsEnum inputsEnum) {

		var input = inputsEnum.getInput();

		output.reply(event, input);

		verify(event).replyChoiceLongs(listLongArgumentCaptor.capture());
		verify(callback).queue();

		var choices = listLongArgumentCaptor.getValue();
		Assertions.assertEquals(Commands.INPUT_AUTOCOMPLETE_OPTIONS.get(input), choices);
	}

}
