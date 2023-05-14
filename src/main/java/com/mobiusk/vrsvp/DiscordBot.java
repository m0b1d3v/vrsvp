package com.mobiusk.vrsvp;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDABuilder;


@RequiredArgsConstructor
public class DiscordBot {

	private final JDABuilder jdaBuilder;

	public static JDABuilder create(String discordBotToken) {
		return JDABuilder.createDefault(discordBotToken);
	}

	public void configure() {
		// Empty
	}

	public void start() throws InterruptedException {
		jdaBuilder.build().awaitReady();
	}

}
