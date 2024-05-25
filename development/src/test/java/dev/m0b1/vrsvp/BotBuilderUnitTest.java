package dev.m0b1.vrsvp;

import dev.m0b1.vrsvp.properties.Properties;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BotBuilderUnitTest extends TestBase {

	@InjectMocks private BotBuilder botBuilder;

	@Spy private Properties properties;

	@Test
	void startingDiscordBotWithoutTokenThrowsException() {
		ensureException(null, IllegalArgumentException.class, "Token may not be null");
	}

	@Test
	void startingDiscordBotWithInvalidTokenThrowsException() {
		ensureException("invalid", InvalidTokenException.class, "The provided token is invalid!");
	}

	// Test utility method(s)

	private void ensureException(String token, Class<? extends Throwable> exceptionType, String message) {

		properties.setBotSecretToken(token);

		var ex = assertThrows(exceptionType, () -> botBuilder.jda());
		assertEquals(message, ex.getMessage());
	}

}
