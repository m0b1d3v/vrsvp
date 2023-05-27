package com.mobiusk.vrsvp.util;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
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

	@Test
	void getEphemeralMessageSourceFailsIfInsufficientPermissions() {

		var guild = mock(Guild.class);
		var permission = mock(Permission.class);
		var exception = new InsufficientPermissionException(guild, permission);

		setupFetcher(message);
		when(messageChannel.retrieveMessageById(anyLong())).thenThrow(exception);

		assertNull(Fetcher.getEphemeralMessageSource(message, messageChannel));

		verify(messageChannel).retrieveMessageById(anyLong());
		verify(restActionMessage, never()).complete();
	}

}
