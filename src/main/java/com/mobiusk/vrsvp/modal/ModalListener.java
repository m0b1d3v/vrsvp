package com.mobiusk.vrsvp.modal;

import com.mobiusk.vrsvp.util.Fetcher;
import com.mobiusk.vrsvp.util.Formatter;
import com.mobiusk.vrsvp.util.GateKeeper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

		log.info(
			Formatter.logMarkers(event).and(Formatter.logMarker("modalId", event.getModalId())),
			"Modal submission received"
		);

		var actionId = event.getModalId();
		var modalEnum = ModalEnum.getById(actionId);
		if (modalEnum == ModalEnum.UNKNOWN) {
			reply.ephemeral(event, "Input not recognized.");
			return;
		}

		if (GateKeeper.accessDenied(event)) {
			log.warn(Formatter.logMarkers(event), "Modal submission denied");
			reply.ephemeral(event, "Access denied.");
			return;
		}

		var textInput = getModalTextInput(event);
		if (textInput == null) {
			reply.ephemeral(event, "No text input received.");
			return;
		}

		if (modalEnum == ModalEnum.EVENT_CREATION) {
			reply.createEmbedFormFromAdmin(event, textInput);
		} else {

			var messageSource = Fetcher.getEphemeralMessageSource(event.getMessage(), event.getMessageChannel());
			if (messageSource == null) {
				log.warn(Formatter.logMarkers(event), Formatter.FORM_NOT_FOUND_REPLY);
				reply.ephemeral(event, Formatter.FORM_NOT_FOUND_REPLY);
				return;
			}

			reply.editEmbedDescriptionFromAdmin(event, messageSource, textInput);
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

}
