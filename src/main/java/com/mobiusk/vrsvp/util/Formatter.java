package com.mobiusk.vrsvp.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.logstash.logback.marker.LogstashMarker;

import static net.logstash.logback.marker.Markers.append;

@UtilityClass
public class Formatter {

	public static final String REPLY_PREFIX = "---\n";
	public static final String REPLY_SUFFIX = "\n---";

	public static String replies(String message) {
		return REPLY_PREFIX + message + REPLY_SUFFIX;
	}

	public static LogstashMarker logMarker(String key, Object value) {
		return append(key, value);
	}

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
