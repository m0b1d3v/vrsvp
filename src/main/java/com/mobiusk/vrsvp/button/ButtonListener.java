package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.util.Fetcher;
import com.mobiusk.vrsvp.util.Formatter;
import com.mobiusk.vrsvp.util.GateKeeper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
@Slf4j
public class ButtonListener extends ListenerAdapter {

	// Class constructor field(s)
	private final ButtonReply reply;

	/**
	 * Direct all button presses from bot messages based on the button ID.
	 *
	 * Button IDs sometimes include extra context by splitting information with ":", like "rsvp:1".
	 */
	@Override
	public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {

		log.atInfo().setMessage("RSVP button received")
			.addMarker(Formatter.logMarkers(event))
			.addMarker(Formatter.logMarker("button", event.getComponentId()))
			.log();

		var buttonIdAction = getButtonInteractionAction(event);
		var buttonIdContext = getButtonInteractionContext(event);

		if (buttonIdAction.contains(ButtonEnum.EDIT.getId()) && GateKeeper.accessDenied(event)) {

			log.atWarn().setMessage("RSVP edit denied")
				.addMarker(Formatter.logMarkers(event))
				.addMarker(Formatter.logMarker("button", buttonIdAction))
				.log();

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
			log.warn(Formatter.logMarkers(event), "Message source not found");
			reply.ephemeral(event, Formatter.FORM_NOT_FOUND_REPLY);
		}

		return rsvp;
	}

}
