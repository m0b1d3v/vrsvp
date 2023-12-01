package dev.m0b1.vrsvp;

import dev.m0b1.vrsvp.logging.ServiceDiscord;
import dev.m0b1.vrsvp.logging.ServiceLog;
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
		if ( ! dsn.isEmpty()) {
			Sentry.captureMessage("Error monitoring initialized");
		}
	}

	private static void startDiscordBot() throws InterruptedException {

		var serviceDiscord = new ServiceDiscord();
		var serviceLog = new ServiceLog(serviceDiscord);

		var javaDiscordApi = Bot.create(DISCORD_BOT_SECRET_TOKEN);
		var discordBot = new Bot(javaDiscordApi, serviceLog);
		discordBot.start();
	}

}
