package com.mobiusk.vrsvp.autocomplete;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.input.InputsEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutoCompleteReplyUnitTest extends TestBase {

	@InjectMocks private AutoCompleteReply reply;

	@BeforeEach
	public void beforeEach() {
		when(commandAutoCompleteEvent.replyChoiceLongs(anyCollection())).thenReturn(autoCompleteCallbackAction);
	}

	@Test
	void unexpectedCommandAutoCompleteEventsAreIgnored() {

		var fieldNames = List.of("unknown-field", InputsEnum.START.getInput());

		for (var fieldName : fieldNames) {
			reply.run(commandAutoCompleteEvent, fieldName);
		}

		verify(commandAutoCompleteEvent, never()).replyChoiceLongs(anyCollection());
		verify(autoCompleteCallbackAction, never()).queue();
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

		reply.run(commandAutoCompleteEvent, input);

		verify(commandAutoCompleteEvent).replyChoiceLongs(listLongArgumentCaptor.capture());
		verify(autoCompleteCallbackAction).queue();

		var choices = listLongArgumentCaptor.getValue();
		Assertions.assertEquals(AutoCompleteReply.OPTIONS.get(input), choices);
	}

}
