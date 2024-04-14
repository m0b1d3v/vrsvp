package dev.m0b1.vrsvp.modal;

import dev.m0b1.vrsvp.util.Mdc;
import dev.m0b1.vrsvp.properties.Properties;
import dev.m0b1.vrsvp.util.Fetcher;
import dev.m0b1.vrsvp.util.GateKeeper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;

@RequiredArgsConstructor
@Service
@Slf4j
public class ModalListener extends ListenerAdapter {

	// Class constructor field(s)
	private final ModalReply reply;

	/**
	 * Gather all text input from modals and direct it as needed based on the input field ID.
	 */
	@Override
	public void onModalInteraction(@Nonnull ModalInteractionEvent event) {

		Mdc.put(event);

		log.atInfo()
			.setMessage("Modal submission received")
			.addKeyValue("modalId", event.getModalId())
			.log();

		var actionId = event.getModalId();
		var modalEnum = ModalEnum.getById(actionId);
		if (modalEnum == ModalEnum.UNKNOWN) {
			reply.ephemeral(event, "Input not recognized.");
			return;
		}

		if (GateKeeper.accessDenied(event)) {

			log.warn("Modal submission denied");

			reply.ephemeral(event, "Access denied.");
			return;
		}

		var textInput = getModalTextInput(event);
		if (textInput == null) {
			reply.ephemeral(event, "No text input received.");
			return;
		}

		if (modalEnum == ModalEnum.EVENT_CREATION) {
			reply.createRsvpFromAdmin(event, textInput);
		} else {

			var messageSource = Fetcher.getEphemeralMessageSource(event.getMessage(), event.getMessageChannel());
			if (messageSource == null) {

				log.warn("Message source not found");

				reply.ephemeral(event, Properties.FORM_NOT_FOUND_REPLY);
				return;
			}

			reply.editRsvpFromAdmin(event, messageSource, textInput);
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
