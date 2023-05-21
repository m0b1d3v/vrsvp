package com.mobiusk.vrsvp.util;

import com.mobiusk.vrsvp.TestBase;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FetcherUnitTest extends TestBase {

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(Fetcher.class);
	}

	@Test
	void getEphemeralMessageSourceFailsIfMessageIsNull() {
		assertNull(Fetcher.getEphemeralMessageSource(null, messageChannel));
		verify(messageChannel, never()).retrieveMessageById(anyLong());
	}

	@Test
	void getEphemeralMessageSourceFailsIfMessageReferenceIsNull() {

		when(message.getMessageReference()).thenReturn(null);

		assertNull(Fetcher.getEphemeralMessageSource(message, messageChannel));

		verify(message).getMessageReference();
		verify(messageChannel, never()).retrieveMessageById(anyLong());
	}

	@Test
	void getEphemeralMessageSourceFailsIfMessageRetrievalDoesNotComplete() {

		setupFetcher(null);

		assertNull(Fetcher.getEphemeralMessageSource(message, messageChannel));

		verify(messageChannel).retrieveMessageById(1L);
		verify(restActionMessage).onErrorMap(any(Function.class));
		verify(restActionMessage).complete();
	}

}
