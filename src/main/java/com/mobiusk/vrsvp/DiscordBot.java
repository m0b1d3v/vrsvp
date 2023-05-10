package com.mobiusk.vrsvp;

import net.dv8tion.jda.api.JDABuilder;

public class DiscordBot {

	private static final String DISCORD_BOT_SECRET_TOKEN = System.getenv("VRSVP_DISCORD_BOT_SECRET_TOKEN");

	private JDABuilder jdaBuilder;

	public void create() {
		jdaBuilder = JDABuilder.createDefault(DISCORD_BOT_SECRET_TOKEN);
	}

	public void configure() {
		// Empty
	}

	public void start() throws InterruptedException {
		jdaBuilder.build().awaitReady();
	}

}
