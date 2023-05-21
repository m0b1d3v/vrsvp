package com.mobiusk.vrsvp.util;

import com.mobiusk.vrsvp.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class FormatterUnitTest extends TestBase {

	@BeforeEach
	public void beforeEach() {
		when(genericInteractionCreateEvent.getUser()).thenReturn(user);
		when(user.getName()).thenReturn("Testing");
	}

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(Formatter.class);
	}

	@Test
	void logMarkerBuild() {
		var result = Formatter.logMarker("key", 3);
		assertEquals("key=3", result.toString());
	}

	@Test
	void logMarkersBuildForInteractionCreateEventWithoutGuildAndChannelName() {

		when(genericInteractionCreateEvent.getGuild()).thenReturn(null);
		when(genericInteractionCreateEvent.getChannel()).thenReturn(null);

		var result = Formatter.logMarkers(genericInteractionCreateEvent);

		assertEquals("server=null, channel=null, user=Testing", result.toString());
	}

	@Test
	void logMarkersBuildForInteractionCreateEventWithGuildAndChannelName() {

		when(guild.getName()).thenReturn("guildName");
		when(genericInteractionCreateEvent.getGuild()).thenReturn(guild);

		when(channel.getName()).thenReturn("channelName");
		when(genericInteractionCreateEvent.getChannel()).thenReturn(channel);

		var result = Formatter.logMarkers(genericInteractionCreateEvent);

		assertEquals("server=guildName, channel=channelName, user=Testing", result.toString());
	}

}
