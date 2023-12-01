package dev.m0b1.vrsvp.modal;

import dev.m0b1.vrsvp.button.ButtonUi;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import javax.annotation.Nonnull;
import java.util.Collections;

public class ModalReply {

	/**
	 * Build an RSVP form everyone can see with a given number of time slot, each with an index and incremented timestamp title.
	 */
	public void createRsvpFromAdmin(@Nonnull ModalInteractionEvent event, String description) {
		event.reply(description)
			.addActionRow(ButtonUi.buildRsvpActionPrompts())
			.queue();
	}

	/**
	 * Edit a message by changing the description and then edit the original ephemeral message to confirm the action.
	 */
	public void editRsvpFromAdmin(
		@Nonnull ModalInteractionEvent event,
		@Nonnull Message message,
		String description
	) {

		message.editMessageEmbeds(Collections.emptyList())
			.setContent(description)
			.queue();

		event.editMessage("Description has been updated.").queue();
	}

	/**
	 * Generic ephemeral reply in response to a modal submission, mostly for validation errors or development feedback.
	 */
	public void ephemeral(@Nonnull ModalInteractionEvent event, String message) {
		event.reply(message).setEphemeral(true).queue();
	}

}
