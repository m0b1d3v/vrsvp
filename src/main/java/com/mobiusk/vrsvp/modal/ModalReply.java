package com.mobiusk.vrsvp.modal;

import com.mobiusk.vrsvp.embed.EmbedUi;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class ModalReply {

	// Class constructor field(s)
	private final EmbedUi embedUi;

	/**
	 * Edit a message by changing the description and then edit the original ephemeral message to confirm the action.
	 */
	public void editEventDescription(
		@Nonnull ModalInteractionEvent event,
		@Nonnull Message message,
		String description
	) {

		var embed = embedUi.editEmbedDescriptionFromAdmin(message, description);
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
