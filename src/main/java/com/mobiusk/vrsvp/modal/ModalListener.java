package com.mobiusk.vrsvp.modal;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
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

		var textInput = getModalTextInput(event);
		if (textInput == null) {
			return;
		}

		var rsvp = getEphemeralModalEventSource(event);
		if (rsvp == null) {
			return;
		}

		var modalEnum = ModalEnum.getById(actionId);
		switch (modalEnum) {
			case DESCRIPTION -> handleDescriptionModalSubmission(event, rsvp, textInput);
			case EMBED -> handleEmbedModalSubmission(event, rsvp, textInput);
			case FIELD_TITLE -> handleFieldTitleModalSubmission(event, rsvp, textInput);
			case FIELD_VALUE -> handleFieldValueModalSubmission(event, rsvp, textInput);
			default -> reply.ephemeral(event, "Input not recognized");
		}
	}

	private String[] getModalInteractionId(@Nonnull ModalInteractionEvent event) {
		var buttonId = event.getModalId();
		return buttonId.split(":");
	}

	private String getModalInteractionAction(@Nonnull ModalInteractionEvent event) {
		var buttonId = getModalInteractionId(event);
		return buttonId[0];
	}

	private Integer getModalInteractionContext(@Nonnull ModalInteractionEvent event) {

		var buttonId = getModalInteractionId(event);
		if (buttonId.length < 2) {
			return null;
		}

		var contextIndex = buttonId[1];
		return Integer.parseInt(contextIndex);
	}

	private void handleDescriptionModalSubmission(
		@Nonnull ModalInteractionEvent event,
		@Nonnull Message message,
		String textInput
	) {
		reply.editDescription(event, message, textInput);
	}

	private void handleEmbedModalSubmission(
		@Nonnull ModalInteractionEvent event,
		@Nonnull Message message,
		String textInput
	) {

		if (accessDenied(event)) {
			return;
		}

		var embedIndex = getModalInteractionContext(event);
		if (embedIndex == null) {
			reply.ephemeral(event, "Input not recognized.");
		} else {
			reply.editEmbed(event, message, textInput, embedIndex);
		}
	}

	private void handleFieldTitleModalSubmission(
		@Nonnull ModalInteractionEvent event,
		@Nonnull Message message,
		String textInput
	) {

		if (accessDenied(event)) {
			return;
		}

		var fieldIndex = getModalInteractionContext(event);
		if (fieldIndex == null) {
			reply.ephemeral(event, "Input not recognized.");
		} else {
			reply.editFieldTitle(event, message, textInput, fieldIndex);
		}
	}

	private void handleFieldValueModalSubmission(
		@Nonnull ModalInteractionEvent event,
		@Nonnull Message message,
		String textInput
	) {

		if (accessDenied(event)) {
			return;
		}

		var fieldIndex = getModalInteractionContext(event);
		if (fieldIndex == null) {
			reply.ephemeral(event, "Input not recognized.");
		} else {
			reply.editFieldValue(event, message, textInput, fieldIndex);
		}
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
