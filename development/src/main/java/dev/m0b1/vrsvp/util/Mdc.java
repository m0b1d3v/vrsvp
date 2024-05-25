package dev.m0b1.vrsvp.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.slf4j.MDC;

@Slf4j
@UtilityClass
public final class Mdc {

	/**
	 * Put guild (server), channel, and user information into MDC for logging.
	 */
	public static void put(GenericInteractionCreateEvent event) {
		if (event != null) {
			putGuild(event);
			putChannel(event);
			putUser(event);
		}
	}

	private static void putGuild(GenericInteractionCreateEvent event) {
		var guild = event.getGuild();
		var guildName = guild == null ? null : guild.getName();
		MDC.put("server", guildName);
	}

	private static void putChannel(GenericInteractionCreateEvent event) {
		var channel = event.getChannel();
		var channelName = channel == null ? null : channel.getName();
		MDC.put("channel", channelName);
	}

	private static void putUser(GenericInteractionCreateEvent event) {
		var userName = event.getUser().getName();
		MDC.put("user", userName);
	}

}
