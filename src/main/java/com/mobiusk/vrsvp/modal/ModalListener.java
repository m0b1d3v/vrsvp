package com.mobiusk.vrsvp.modal;

import com.mobiusk.vrsvp.util.Formatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
@Slf4j
public class ModalListener extends ListenerAdapter {

	// Class constructor field(s)
	private final ModalReply reply;

	/**
	 * Gather all text input from modals and direct it as needed based on the input field ID.
	 */
	@Override
	public void onModalInteraction(@Nonnull ModalInteractionEvent event) {

		event.deferEdit().queue();

		var actionId = getModalInteractionAction(event);

		log.info(
			Formatter.logMarkers(event).and(Formatter.logMarker("modalId", event.getModalId())),
			"Modal submission received"
		);

		var textInput = getModalTextInput(event);
		if (textInput == null) {
			return;
		}

		var rsvp = getEphemeralModalEventSource(event);
		if (rsvp == null) {
			return;
		}

		if (accessDenied(event)) {
			return;
		}

		var modalEnum = ModalEnum.getById(actionId);
		if (modalEnum == ModalEnum.EVENT_DESCRIPTION) {
			handleEventDescriptionModalSubmission(event, rsvp, textInput);
		} else {
			reply.ephemeral(event, "Input not recognized");
		}
	}

	private String getModalInteractionAction(@Nonnull ModalInteractionEvent event) {
		var buttonId = event.getModalId();
		var idParts = buttonId.split(":");
		return idParts[0];
	}

	private void handleEventDescriptionModalSubmission(
		@Nonnull ModalInteractionEvent event,
		@Nonnull Message message,
		String textInput
	) {
		reply.editEventDescription(event, message, textInput);
	}

	private String getModalTextInput(@Nonnull ModalInteractionEvent event) {

		String textInput = null;

		var modalMapping = event.getValue(event.getModalId());
		if (modalMapping != null) {
			textInput = modalMapping.getAsString();
		}

		return textInput;
	}

	// Any changes here should be propagated to the similar method in ModalListener (no common parent = copied code)
	private Message getEphemeralModalEventSource(ModalInteractionEvent event) {

		var message = event.getMessage();
		if (message == null) {
			return null;
		}

		var eventMessageReference = event.getMessage().getMessageReference();
		if (eventMessageReference == null) {
			return null;
		}

		return event
			.getChannel()
			.retrieveMessageById(eventMessageReference.getMessageIdLong())
			.complete();
	}

	private boolean accessDenied(@Nonnull ModalInteractionEvent event) {

		var member = event.getMember();

		var accessDenied = member != null && ! member.hasPermission(Permission.ADMINISTRATOR);
		if (accessDenied) {
			reply.ephemeral(event, "Access denied.");
		}

		return accessDenied;
	}

}
