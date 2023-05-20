package com.mobiusk.vrsvp;

import io.sentry.Sentry;

import java.util.Objects;

public class Main {

	private static final String DISCORD_BOT_SECRET_TOKEN = System.getenv("VRSVP_DISCORD_BOT_SECRET_TOKEN");
	private static final String SENTRY_DSN = System.getenv("SENTRY_DSN");

	public static void main(String[] args) throws InterruptedException {
		startErrorMonitoring();
		startDiscordBot();
	}

	private static void startErrorMonitoring() {
		var dsn = Objects.requireNonNullElse(SENTRY_DSN, "");
		Sentry.init(options -> options.setDsn(dsn));
	}

	private static void startDiscordBot() throws InterruptedException {
		var javaDiscordApi = Bot.create(DISCORD_BOT_SECRET_TOKEN);
		var discordBot = new Bot(javaDiscordApi);
		discordBot.start();
	}

}
