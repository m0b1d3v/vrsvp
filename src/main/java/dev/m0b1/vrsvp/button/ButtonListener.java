package dev.m0b1.vrsvp.button;

import dev.m0b1.vrsvp.logging.LogData;
import dev.m0b1.vrsvp.logging.ServiceLog;
import dev.m0b1.vrsvp.properties.Properties;
import dev.m0b1.vrsvp.util.Fetcher;
import dev.m0b1.vrsvp.util.GateKeeper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ButtonListener extends ListenerAdapter {

	// Class constructor field(s)
	private final ButtonReply reply;
	private final ServiceLog serviceLog;

	/**
	 * Direct all button presses from bot messages based on the button ID.
	 *
	 * Button IDs sometimes include extra context by splitting information with ":", like "rsvp:1".
	 */
	@Override
	public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {

		serviceLog.run(LogData.builder()
			.level(Level.INFO)
			.message("RSVP button received")
			.event(event)
			.markers(Map.of("button", event.getComponentId()))
		);

		var buttonIdAction = getButtonInteractionAction(event);
		var buttonIdContext = getButtonInteractionContext(event);

		if (buttonIdAction.contains(ButtonEnum.EDIT.getId()) && GateKeeper.accessDenied(event)) {

			serviceLog.run(LogData.builder()
				.level(Level.WARN)
				.message("RSVP edit denied")
				.event(event)
				.markers(Map.of("button", buttonIdAction))
			);

			reply.ephemeral(event, "Access denied.");
			return;
		}

		var buttonEnum = ButtonEnum.getById(buttonIdAction);
		switch (buttonEnum) {
			case EDIT -> reply.editInterest(event);
			case EDIT_EVENT_ACTIVE -> handleEditEventActiveButtonPress(event);
			case EDIT_EVENT_DESCRIPTION -> handleEditEventDescriptionButtonPress(event);
			case RSVP -> handleRsvpButtonPress(event, buttonIdContext);
			default -> reply.ephemeral(event, "Input not recognized.");
		}
	}

	private String[] getButtonInteractionId(@Nonnull ButtonInteractionEvent event) {
		var buttonId = event.getComponentId();
		return buttonId.split(":");
	}

	private String getButtonInteractionAction(@Nonnull ButtonInteractionEvent event) {
		var buttonId = getButtonInteractionId(event);
		return buttonId[0];
	}

	private Integer getButtonInteractionContext(@Nonnull ButtonInteractionEvent event) {

		var buttonId = getButtonInteractionId(event);
		if (buttonId.length < 2) {
			return null;
		}

		var contextIndex = buttonId[1];
		return Integer.parseInt(contextIndex);
	}

	private void handleEditEventActiveButtonPress(@Nonnull ButtonInteractionEvent event) {

		var rsvp = getMessageSource(event);
		if (rsvp != null) {
			reply.editToggleRsvpActive(event, rsvp);
		}
	}

	private void handleEditEventDescriptionButtonPress(@Nonnull ButtonInteractionEvent event) {

		var rsvp = getMessageSource(event);
		if (rsvp != null) {
			reply.editEventDescription(event, rsvp);
		}
	}

	private void handleRsvpButtonPress(@Nonnull ButtonInteractionEvent event, Integer context) {
		if (context != null) {

			var rsvp = getMessageSource(event);
			if (rsvp != null) {
				reply.rsvpToggle(event, rsvp, context);
			}

		} else {
			reply.rsvpInterest(event);
		}
	}

	private Message getMessageSource(@Nonnull ButtonInteractionEvent event) {

		var rsvp = Fetcher.getEphemeralMessageSource(event.getMessage(), event.getMessageChannel());
		if (rsvp == null) {

			serviceLog.run(LogData.builder()
				.level(Level.WARN)
				.message("Message source not found")
				.event(event)
			);

			reply.ephemeral(event, Properties.FORM_NOT_FOUND_REPLY);
		}

		return rsvp;
	}

}
