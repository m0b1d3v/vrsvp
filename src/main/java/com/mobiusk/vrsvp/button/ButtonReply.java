package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.modal.ModalEnum;
import com.mobiusk.vrsvp.embed.EmbedUi;
import com.mobiusk.vrsvp.modal.ModalUi;
import com.mobiusk.vrsvp.util.Formatter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import javax.annotation.Nonnull;
import java.util.Objects;

@RequiredArgsConstructor
public class ButtonReply {

	// Class constructor field(s)
	private final ButtonUi buttonUi;
	private final EmbedUi embedUi;
	private final ModalUi modalUi;

	/**
	 * Replies with an ephemeral list of buttons admins can click to start editing part of an RSVP form.
	 */
	public void edit(@Nonnull ButtonInteractionEvent event, int embedCount) {

		var editActions = buttonUi.buildEditActionPrompts(embedCount);

		var message = Formatter.replies("""
			Use these buttons to edit the RSVP form.
			The numbered buttons correspond to blocks, not slots!""");

		event.reply(message)
			.setEphemeral(true)
			.setComponents(editActions)
			.queue();
	}

	/**
	 * Replies with an ephemeral list of buttons admins can click to start editing part of an RSVP form.
	 */
	public void editEventDescription(@Nonnull ButtonInteractionEvent event, @Nonnull Message message) {
		var currentText = message.getContentDisplay();
		var modal = modalUi.editText(ModalEnum.EVENT_DESCRIPTION, currentText, 500, null);
		event.replyModal(modal).queue();
	}

	/**
	 * Launch a modal with one text input to prompt an admin for a block edit.
	 */
	public void editEmbedDescription(@Nonnull ButtonInteractionEvent event, @Nonnull Message message, int embedIndex) {
		var embeds = message.getEmbeds();
		var currentText = Objects.requireNonNullElse(embeds.get(embedIndex).getDescription(), "");
		var modal = modalUi.editText(ModalEnum.EMBED_DESCRIPTION, currentText, 5500 / embeds.size(), embedIndex);
		event.replyModal(modal).queue();
	}

	/**
	 * Replies with an ephemeral list of buttons users can click to toggle RSVP state for slots.
	 */
	public void rsvpInterest(@Nonnull ButtonInteractionEvent event, int slotsAvailable) {

		var buttonRows = buttonUi.buildIndexedButtonActionRows(ButtonEnum.RSVP.getId(), slotsAvailable);

		var message = Formatter.replies("Use these buttons to toggle your RSVP for any slot.");

		event.reply(message)
			.setEphemeral(true)
			.setComponents(buttonRows)
			.queue();
	}

	/**
	 * Toggles RSVP state for given user and slot and then adjusts source ephemeral message to reflect the change.
	 */
	public void rsvpToggle(
		@Nonnull ButtonInteractionEvent event,
		@Nonnull Message message,
		int slotIndex
	) {

		var existingEmbeds = message.getEmbeds();
		var editedEmbeds = embedUi.editEmbedDescriptionFromRSVP(existingEmbeds, userMention, slotIndex);
		message.editMessageEmbeds(editedEmbeds).queue();
		var userMention = event.getUser().getAsMention();

		var reply = Formatter.replies(String.format("RSVP state toggled for slot #%d", slotIndex + 1));

		event.editMessage(reply)
			.queue();
	}

	/**
	 * Generic ephemeral reply in response to a button press, mostly for validation errors or development feedback.
	 */
	public void ephemeral(@Nonnull ButtonInteractionEvent event, String message) {
		event.reply(message).setEphemeral(true).queue();
	}

}
