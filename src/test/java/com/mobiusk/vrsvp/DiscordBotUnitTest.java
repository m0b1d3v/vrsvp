package com.mobiusk.vrsvp;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiscordBotUnitTest extends TestBase {

	@InjectMocks
	private DiscordBot discordBot;

	@Spy
	private JDABuilder jdaBuilder = DiscordBot.create("Testing");

	@Mock
	private JDA jda;

	@BeforeEach
	public void beforeEach() {

		doReturn(jda).when(jdaBuilder).build();

	}

	@Test
	void startingDiscordBotWithoutTokenThrowsException() {

		// In direct contradiction to what we set up in beforeEach for other tests, check this edge case
		doCallRealMethod().when(jdaBuilder).build();

		jdaBuilder.setToken(null);
		var ex = assertThrows(IllegalArgumentException.class, () -> discordBot.start());
		assertEquals("Token may not be null", ex.getMessage());
	}

	@Test
	void startingDiscordBotWaitsForItToBeReady() throws InterruptedException {

		startDiscordBot();

		verify(jdaBuilder).build();
		verify(jda).awaitReady();
	}

	// Test utility method(s)

	private void startDiscordBot() {
		try {
			discordBot.start();
		} catch (InterruptedException e) {
			fail("Discord bot should have started without interruption for this test case", e);
		}
	}

}
