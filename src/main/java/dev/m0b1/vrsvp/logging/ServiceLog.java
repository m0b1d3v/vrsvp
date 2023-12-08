package dev.m0b1.vrsvp.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.logstash.logback.marker.LogstashMarker;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.logstash.logback.marker.Markers.append;

@RequiredArgsConstructor
@Service
@Slf4j
public class ServiceLog {

	private final ServiceDiscord serviceDiscord;

	public void run(LogData.LogDataBuilder logData) {
		run(logData.build());
	}

	public void run(LogData logData) {

		var discordData = new LinkedHashMap<String, Object>();

		var level = logData.getLevel();
		var logEvent = log.atLevel(level);
		discordData.put("level", level.toString());

		var message = logData.getMessage();
		if (message != null && !message.isBlank()) {
			logEvent = logEvent.setMessage(message);
			discordData.put("message", message);
		}

		var throwable = logData.getThrowable();
		if (throwable != null) {
			logEvent = logEvent.setCause(throwable);
			discordData.put("cause", throwable.toString());
		}

		var event = logData.getEvent();
		if (event != null) {
			logEvent = logEvent.addMarker(logMarkers(event, discordData));
		}

		var markers = logData.getMarkers();
		if (MapUtils.isNotEmpty(markers)) {
			for (var entry : markers.entrySet()) {
				var k = entry.getKey();
				var v = entry.getValue();
				logEvent = logEvent.addMarker(append(k, v));
				discordData.put(k, v);
			}
		}

		logEvent.log();

		serviceDiscord.run(discordData);
	}

	/**
	 * Create key-value log markers for JSON logging containing guild (server), channel, and user information.
	 */
	private static LogstashMarker logMarkers(GenericInteractionCreateEvent event, Map<String, Object> data) {
		return logGuildMarker(event, data)
			.and(logChannelMarker(event, data))
			.and(logUserMarker(event, data));
	}

	private static LogstashMarker logGuildMarker(GenericInteractionCreateEvent event, Map<String, Object> data) {
		var guild = event.getGuild();
		String guildName = guild == null ? null : guild.getName();
		data.put("server", guildName);
		return logMarker("server", guildName);
	}

	private static LogstashMarker logChannelMarker(GenericInteractionCreateEvent event, Map<String, Object> data) {
		var channel = event.getChannel();
		String channelName = channel == null ? null : channel.getName();
		data.put("channel", channelName);
		return logMarker("channel", channelName);
	}

	private static LogstashMarker logUserMarker(GenericInteractionCreateEvent event, Map<String, Object> data) {
		var userName = event.getUser().getName();
		data.put("user", userName);
		return logMarker("user", userName);
	}

	/**
	 * Create a singular key-value log marker for JSON logging.
	 */
	private static LogstashMarker logMarker(String key, Object value) {
		return append(key, value);
	}

}
