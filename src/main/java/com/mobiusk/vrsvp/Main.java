package com.mobiusk.vrsvp;

public class Main {

	private static final String DISCORD_BOT_SECRET_TOKEN = System.getenv("VRSVP_DISCORD_BOT_SECRET_TOKEN");

	public static void main(String[] args) throws InterruptedException {

		var javaDiscordApi = Bot.create(DISCORD_BOT_SECRET_TOKEN);

		var discordBot = new Bot(javaDiscordApi);
		discordBot.start();
	}

}
