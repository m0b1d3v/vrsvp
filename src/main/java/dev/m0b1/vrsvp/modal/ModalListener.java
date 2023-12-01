package dev.m0b1.vrsvp.modal;

import dev.m0b1.vrsvp.logging.LogData;
import dev.m0b1.vrsvp.logging.ServiceLog;
import dev.m0b1.vrsvp.util.Fetcher;
import dev.m0b1.vrsvp.util.Formatter;
import dev.m0b1.vrsvp.util.GateKeeper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.event.Level;

import javax.annotation.Nonnull;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class ModalListener extends ListenerAdapter {

	// Class constructor field(s)
	private final ModalReply reply;
	private final ServiceLog serviceLog;

	/**
	 * Gather all text input from modals and direct it as needed based on the input field ID.
	 */
	@Override
	public void onModalInteraction(@Nonnull ModalInteractionEvent event) {

		serviceLog.run(LogData.builder()
			.level(Level.INFO)
			.message("Modal submission received")
			.event(event)
			.markers(Map.of("modalId", event.getModalId()))
		);

		var actionId = event.getModalId();
		var modalEnum = ModalEnum.getById(actionId);
		if (modalEnum == ModalEnum.UNKNOWN) {
			reply.ephemeral(event, "Input not recognized.");
			return;
		}

		if (GateKeeper.accessDenied(event)) {

			serviceLog.run(LogData.builder()
				.level(Level.WARN)
				.message("Modal submission denied")
				.event(event)
			);

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

				serviceLog.run(LogData.builder()
					.level(Level.WARN)
					.message("Message source not found")
					.event(event)
				);

				reply.ephemeral(event, Formatter.FORM_NOT_FOUND_REPLY);
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
