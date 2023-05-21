package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.util.Formatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
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

		var buttonInteractionAction = getButtonInteractionAction(event);

		log.info(
			Formatter.logMarkers(event).and(Formatter.logMarker("button", event.getComponentId())),
			"RSVP button received"
		);

		if (buttonInteractionAction.contains(ButtonEnum.EDIT.getId()) && accessDenied(event)) {

			log.warn(
				Formatter.logMarkers(event).and(Formatter.logMarker("button", buttonInteractionAction)),
				"RSVP edit denied"
			);

			return;
		}

		var buttonEnum = ButtonEnum.getById(buttonInteractionAction);
		switch (buttonEnum) {
			case EDIT -> handleAdminEditButtonPress(event);
			case EDIT_EVENT_ACTIVE -> handleEditEventActiveToggleButtonPress(event);
			case EDIT_EVENT_DESCRIPTION -> handleEditEventDescriptionButtonPress(event);
			case RSVP -> handleRsvpButtonPress(event);
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

	private void handleAdminEditButtonPress(@Nonnull ButtonInteractionEvent event) {
		reply.edit(event);
	}

	private void handleEditEventActiveToggleButtonPress(@Nonnull ButtonInteractionEvent event) {

		var rsvp = getEphemeralButtonEventSource(event);
		if (rsvp == null) {
			return;
		}

		reply.editToggleRsvpActive(event, rsvp);
	}

	private void handleEditEventDescriptionButtonPress(@Nonnull ButtonInteractionEvent event) {

		var rsvp = getEphemeralButtonEventSource(event);
		if (rsvp == null) {
			return;
		}

		reply.editEventDescription(event, rsvp);
	}

	private void handleRsvpButtonPress(@Nonnull ButtonInteractionEvent event) {

		var slotIndex = getButtonInteractionContext(event);
		if (slotIndex != null) {

			var rsvp = getEphemeralButtonEventSource(event);
			if (rsvp == null) {
				return;
			}

			reply.rsvpToggle(event, rsvp, slotIndex);

		} else {
			reply.rsvpInterest(event);
		}
	}

	// Any changes here should be propagated to the similar method in ModalListener (no common parent = copied code)
	private Message getEphemeralButtonEventSource(ButtonInteractionEvent event) {

		var eventMessageReference = event.getMessage().getMessageReference();
		if (eventMessageReference == null) {
			return null;
		}

		return event.getMessageChannel()
			.retrieveMessageById(eventMessageReference.getMessageIdLong())
			.onErrorMap(ex -> {
				log.warn("Message retrieval attempted on deleted message", ex);
				return null;
			})
			.complete();
	}

	private boolean accessDenied(@Nonnull ButtonInteractionEvent event) {

		var member = event.getMember();

		var accessDenied = member != null && ! member.hasPermission(Permission.ADMINISTRATOR);
		if (accessDenied) {
			reply.ephemeral(event, "Access denied.");
		}

		return accessDenied;
	}

}
