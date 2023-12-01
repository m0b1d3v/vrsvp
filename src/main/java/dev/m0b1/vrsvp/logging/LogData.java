package dev.m0b1.vrsvp.logging;

import lombok.Builder;
import lombok.Data;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.slf4j.event.Level;

import java.util.Map;

@Builder
@Data
public class LogData {

	private Level level;
	private String message;
	private Throwable throwable;
	private GenericInteractionCreateEvent event;
	private Map<String, Object> markers;

}
