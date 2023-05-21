package com.mobiusk.vrsvp.modal;

import com.mobiusk.vrsvp.embed.EmbedUi;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import javax.annotation.Nonnull;

public class ModalReply {

	/**
	 * Edit a message by changing the description and then edit the original ephemeral message to confirm the action.
	 */
	public void editEmbedDescriptionFromAdmin(
		@Nonnull ModalInteractionEvent event,
		@Nonnull Message message,
		String description
	) {

		var embed = EmbedUi.editEmbedDescriptionFromAdmin(message, description);
		message.editMessageEmbeds(embed).queue();

		event.getHook()
			.editOriginal("Description has been updated.")
			.queue();
	}

	/**
	 * Generic ephemeral reply in response to a modal submission, mostly for validation errors or development feedback.
	 */
	public void ephemeral(@Nonnull ModalInteractionEvent event, String message) {
		event.reply(message).setEphemeral(true).queue();
	}

}
