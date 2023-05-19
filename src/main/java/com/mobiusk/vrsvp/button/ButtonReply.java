package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.modal.ModalEnum;
import com.mobiusk.vrsvp.embed.EmbedUi;
import com.mobiusk.vrsvp.modal.ModalUi;
import com.mobiusk.vrsvp.util.Formatter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import javax.annotation.Nonnull;

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
	 * Replies with an ephemeral list of buttons admins can click to start editing part of an RSVP form.
	 */
	public void editDescription(@Nonnull ButtonInteractionEvent event, @Nonnull Message message) {
		var currentText = message.getContentDisplay();
		var modal = modalUi.editText(ModalEnum.DESCRIPTION, currentText, null);
		event.replyModal(modal).queue();
	}

	/**
	 * Launch a modal with one text input to prompt an admin for an embed title.
	 */
	public void editEmbed(@Nonnull ButtonInteractionEvent event, @Nonnull Message message, int embedIndex) {
		var currentText = message.getEmbeds().get(embedIndex).getTitle();
		var modal = modalUi.editText(ModalEnum.EMBED, currentText, embedIndex);
		event.replyModal(modal).queue();
	}

	/**
	 * Launch a modal with one text input to prompt an admin for a slot title.
	 */
	public void editSlotTitle(@Nonnull ButtonInteractionEvent event, @Nonnull Message message, int slotIndex) {

		var field = getFieldForSlotIndex(message, slotIndex);

		var currentText = "";
		if (field != null) {
			currentText = field.getName();
		}

		var modal = modalUi.editText(ModalEnum.FIELD_TITLE, currentText, slotIndex);

		event.replyModal(modal).queue();
	}

	/**
	 * Launch a modal with one text input to prompt an admin for a slot value.
	 */
	public void editSlotValue(@Nonnull ButtonInteractionEvent event, @Nonnull Message message, int slotIndex) {

		var field = getFieldForSlotIndex(message, slotIndex);

		var currentText = "";
		if (field != null) {
			currentText = field.getValue();
		}

		var modal = modalUi.editText(ModalEnum.FIELD_VALUE, currentText, slotIndex);

		event.replyModal(modal).queue();
	}

	/**
	 * Edits original ephemeral message with a list of indexed buttons for generic edit flow usage.
	 */
	public void editIndexedSelectionGeneration(
		@Nonnull ButtonInteractionEvent event,
		String label,
		String actionId,
		int buttonCount
	) {

		var buttonRows = buttonUi.buildIndexedButtonActionRows(actionId, buttonCount);

		var reply = Formatter.replies(String.format("Select the %s number you wish to edit.", label));

		event.editMessage(reply)
			.setComponents(buttonRows)
			.queue();
	}

	/**
	 * Replies with an ephemeral list of buttons users can click to toggle RSVP state for slots.
	 */
	public void rsvp(@Nonnull ButtonInteractionEvent event, int slotsAvailable) {

		var buttonRows = buttonUi.buildIndexedButtonActionRows(ButtonEnum.SIGNUP.getId(), slotsAvailable);

		var message = Formatter.replies("Use these buttons to toggle your RSVP for any slot.");

		event.reply(message)
			.setEphemeral(true)
			.setComponents(buttonRows)
			.queue();
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

	private MessageEmbed.Field getFieldForSlotIndex(@Nonnull Message message, int slotIndex) {

		var slotIndexCounter = 0;
		for (var embed : message.getEmbeds()) {
			for (var field : embed.getFields()) {
				if (slotIndexCounter == slotIndex) {
					return field;
				}
				slotIndexCounter++;
			}
		}

		return null;
	}

}
