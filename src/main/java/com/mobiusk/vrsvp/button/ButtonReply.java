package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.modal.ModalEnum;
import com.mobiusk.vrsvp.embed.EmbedUi;
import com.mobiusk.vrsvp.modal.ModalUi;
import com.mobiusk.vrsvp.util.Formatter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

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
	public void edit(@Nonnull ButtonInteractionEvent event) {

		var buttonRow = buttonUi.buildEditTopLevelActionRow();

		var message = Formatter.replies("Use these buttons to start editing the signup.");

		event.reply(message)
			.setEphemeral(true)
			.addActionRow(buttonRow)
			.queue();
	}

	/**
	 * Returns ephemeral message back to the root of the admin edit flow.
	 */
	public void editBack(@Nonnull ButtonInteractionEvent event) {

		var buttonRow = buttonUi.buildEditTopLevelActionRow();

		var message = Formatter.replies("Use these buttons to start editing the signup.");

		event.editMessage(message)
			.setComponents(ActionRow.of(buttonRow))
			.queue();
	}

	/**
	 * Replies with an ephemeral list of buttons admins can click to start editing part of an RSVP form.
	 */
	public void editEventDescription(@Nonnull ButtonInteractionEvent event, @Nonnull Message message) {
		var currentText = message.getContentDisplay();
		var modal = modalUi.editText(ModalEnum.EVENT_DESCRIPTION, currentText, null);
		event.replyModal(modal).queue();
	}

	/**
	 * Launch a modal with one text input to prompt an admin for an embed title.
	 */
	public void editEmbedTitle(@Nonnull ButtonInteractionEvent event, @Nonnull Message message, int embedIndex) {
		var currentText = message.getEmbeds().get(embedIndex).getTitle();
		var modal = modalUi.editText(ModalEnum.EMBED_TITLE, currentText, embedIndex);
		event.replyModal(modal).queue();
	}

	/**
	 * Launch a modal with one text input to prompt an admin for a slot title.
	 */
	public void editEmbedDescription(@Nonnull ButtonInteractionEvent event, @Nonnull Message message, int embedIndex) {
		var currentText = Objects.requireNonNullElse(message.getEmbeds().get(embedIndex).getDescription(), "");
		var modal = modalUi.editText(ModalEnum.EMBED_DESCRIPTION, currentText, embedIndex);
		event.replyModal(modal).queue();
	}

	/**
	 * Edits original ephemeral message with a list of indexed buttons for block edit flow usage.
	 */
	public void editIndexedBlockSelection(
		@Nonnull ButtonInteractionEvent event,
		String actionId,
		int buttonCount
	) {

		var buttonRows = buttonUi.buildIndexedButtonActionRowsWithBackButton(actionId, buttonCount);

		var reply = Formatter.replies("Select the block number you wish to edit.");

		event.editMessage(reply)
			.setComponents(buttonRows)
			.queue();
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
		String userMention,
		int slotIndex
	) {

		var existingEmbeds = message.getEmbeds();
		var editedEmbeds = embedUi.editEmbedDescriptionFromRSVP(existingEmbeds, userMention, slotIndex);
		message.editMessageEmbeds(editedEmbeds).queue();

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
