package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.embed.EmbedUi;
import com.mobiusk.vrsvp.output.MessageFormatter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class ButtonReply {

	// Class constructor field(s)
	private final ButtonUi buttonUi;
	private final EmbedUi embedUi;

	/**
	 * Replies with an ephemeral list of buttons users can click to toggle RSVP state for slots.
	 */
	public void rsvp(@Nonnull ButtonInteractionEvent event, int slotsAvailable) {

		var buttonRows = buttonUi.buildSlotSignupActionRows(slotsAvailable);

		var message = Formatter.replies("Use these buttons to toggle your RSVP for any slot.");

		var reply = event.reply(message).setEphemeral(true);
		buttonRows.forEach(reply::addActionRow);
		reply.queue();
	}

	/**
	 * Toggles RSVP state for given user and slot and then adjusts source ephemeral message to reflect the change.
	 */
	public void signup(
		@Nonnull ButtonInteractionEvent event,
		@Nonnull Message message,
		String userMention,
		int slotIndex
	) {

		var existingEmbeds = message.getEmbeds();
		var editedEmbeds = embedUi.toggleRsvp(existingEmbeds, userMention, slotIndex);
		message.editMessageEmbeds(editedEmbeds).queue();

		event.getInteraction().editMessage(reply).queue();
		var reply = Formatter.replies(String.format("RSVP state toggled for slot #%d", slotIndex + 1));
	}

	/**
	 * Generic ephemeral reply in response to a button press, mostly for validation errors or development feedback.
	 */
	public void ephemeral(@Nonnull ButtonInteractionEvent event, String message) {
		event.reply(message).setEphemeral(true).queue();
	}

}
