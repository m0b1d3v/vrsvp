package com.mobiusk.vrsvp;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

class DiscordBotUnitTest extends TestBase {

	@InjectMocks
	private DiscordBot discordBot;

	@Spy
	private JDABuilder jdaBuilder = JDABuilder.createDefault("Testing");

	@Mock
	private JDA jda;

	@Test
	void defaultJdaBuilderCreated() {
		discordBot.create();
		assertNotNull(jdaBuilder);
	}

	@Test
	void startingDiscordBotWithoutTokenThrowsException() {
		jdaBuilder.setToken(null);
		var ex = assertThrows(IllegalArgumentException.class, () -> discordBot.start());
		assertEquals("Token may not be null", ex.getMessage());
	}

	@Test
	void startingDiscordBotWaitsForItToBeReady() throws InterruptedException {

		doReturn(jda).when(jdaBuilder).build();

		discordBot.start();

		verify(jdaBuilder).build();
		verify(jda).awaitReady();
	}

}
