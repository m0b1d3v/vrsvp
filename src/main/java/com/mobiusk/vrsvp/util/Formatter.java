package com.mobiusk.vrsvp.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.logstash.logback.marker.LogstashMarker;

import static net.logstash.logback.marker.Markers.append;

@UtilityClass
public class Formatter {

	public static final String FORM_NOT_FOUND_REPLY = "Cannot find event post. Was it deleted? Does the bot have read permissions?";

	/**
	 * Create a singular key-value log marker for JSON logging.
	 */
	public static LogstashMarker logMarker(String key, Object value) {
		return append(key, value);
	}

	/**
	 * Create key-value log markers for JSON logging containing guild (server), channel, and user information.
	 */
	public static LogstashMarker logMarkers(GenericInteractionCreateEvent event) {
		return logGuildMarker(event)
			.and(logChannelMarker(event))
			.and(logUserMarker(event));
	}

	private static LogstashMarker logGuildMarker(GenericInteractionCreateEvent event) {
		var guild = event.getGuild();
		String guildName = guild == null ? null : guild.getName();
		return logMarker("server", guildName);
	}

	private static LogstashMarker logChannelMarker(GenericInteractionCreateEvent event) {
		var channel = event.getChannel();
		String channelName = channel == null ? null : channel.getName();
		return logMarker("channel", channelName);
	}

	private static LogstashMarker logUserMarker(GenericInteractionCreateEvent event) {
		var userName = event.getUser().getName();
		return logMarker("user", userName);
	}

}
