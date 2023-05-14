package com.mobiusk.vrsvp;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

@RequiredArgsConstructor
public class DiscordBot {

	private final JDABuilder jdaBuilder;

	/**
	 * Create a Java Discord API gateway with no intents desired and using low memory by not caching any information.
	 */
	public static JDABuilder create(String discordBotToken) {
		return JDABuilder.createLight(discordBotToken, EnumSet.noneOf(GatewayIntent.class));
	}

	public void configure() {
		// Empty
	}

	public void start() throws InterruptedException {
		jdaBuilder.build().awaitReady();
	}

}
