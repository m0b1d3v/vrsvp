package com.mobiusk.vrsvp.autocomplete;

import com.mobiusk.vrsvp.TestBase;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutoCompleteListenerUnitTest extends TestBase {

	@InjectMocks private AutoCompleteListener listener;

	@Mock private AutoCompleteReply reply;

	@Test
	void commandAutoCompleteEventsIntercepted() {

		when(autoCompleteQuery.getName()).thenReturn("Testing");
		when(commandAutoCompleteEvent.getFocusedOption()).thenReturn(autoCompleteQuery);

		listener.onCommandAutoCompleteInteraction(commandAutoCompleteEvent);

		verify(reply).run(commandAutoCompleteEvent, "Testing");
	}

}
