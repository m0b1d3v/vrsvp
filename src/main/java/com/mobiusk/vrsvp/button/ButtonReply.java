package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.modal.ModalEnum;
import com.mobiusk.vrsvp.embed.EmbedUi;
import com.mobiusk.vrsvp.modal.ModalUi;
import com.mobiusk.vrsvp.util.Parser;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import javax.annotation.Nonnull;
import java.util.LinkedList;
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

		var buttons = buttonUi.buildEditActionPrompts();

		var reply = """
			Use these buttons to edit the RSVP form.
			When editing descriptions, it is suggested to copy the text into your favorite editor for making changes.""";

		event.reply(reply)
			.setEphemeral(true)
			.addActionRow(buttons)
			.queue();
	}

	/**
	 * Toggle the disabled state of the RSVP button on a message.
	 */
	public void editToggleRsvpActive(@Nonnull ButtonInteractionEvent event, @Nonnull Message message) {

		var rsvpButton = message.getButtonById(ButtonEnum.RSVP.getId());
		if (rsvpButton == null) {
			event.editMessage("Could not toggle RSVP button").queue();
			return;
		}

		rsvpButton = rsvpButton.withDisabled( ! rsvpButton.isDisabled());

		var buttons = new LinkedList<>(message.getButtons());
		buttons.set(0, rsvpButton);

		message.editMessageComponents(ActionRow.of(buttons)).queue();

		event.editMessage("RSVP button toggled").queue();
	}

	/**
	 * Launch a modal with one text input to prompt an admin for a complete edit of the event.
	 */
	public void editEventDescription(@Nonnull ButtonInteractionEvent event, @Nonnull Message message) {

		var currentText = Parser.readMessageDescription(message);
		var modal = modalUi.editText(ModalEnum.EVENT_DESCRIPTION, currentText);

		event.replyModal(modal).queue();
	}

	/**
	 * Replies with an ephemeral list of buttons users can click to toggle RSVP state for slots.
	 */
	public void rsvpInterest(@Nonnull ButtonInteractionEvent event) {

		var embedDescription = Parser.readMessageDescription(event.getMessage());
		var slots = Parser.countSlotsInText(embedDescription);
		var buttonRows = buttonUi.buildIndexedButtonActionRows(ButtonEnum.RSVP.getId(), slots);

		event.reply("Use these buttons to toggle your RSVP for any slot.")
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

		var userMention = event.getUser().getAsMention();

		var originalDescription = Parser.readMessageDescription(message);

		var editedEmbed = embedUi.editEmbedDescriptionFromRSVP(message, userMention, slotIndex);
		var editedDescription = Objects.requireNonNullElse(editedEmbed.getDescription(), "");

		if (editedDescription.length() > originalDescription.length()
			&& (rsvpLimitPerPersonExceeded(message, userMention) || rsvpLimitPerSlotExceeded(message, slotIndex))
		) {
			var errorMessage = String.format("Signup limit exceeded, cannot RSVP for slot #%d", slotIndex + 1);
			event.editMessage(errorMessage).queue();
			return;
		}

		message.editMessageEmbeds(editedEmbed).queue();

		var reply = String.format("RSVP state toggled for slot #%d", slotIndex + 1);
		event.editMessage(reply).queue();
	}

	/**
	 * Generic ephemeral reply in response to a button press, mostly for validation errors or development feedback.
	 */
	public void ephemeral(@Nonnull ButtonInteractionEvent event, String message) {
		event.reply(message).setEphemeral(true).queue();
	}

	private boolean rsvpLimitPerPersonExceeded(@Nonnull Message message, String userMention) {

		var description = Parser.readMessageDescription(message);

		var limit = Parser.findRsvpLimitPerPersonInText(description);
		return limit != null && currentRsvpCountForUser(message, userMention) >= limit;
	}

	private boolean rsvpLimitPerSlotExceeded(@Nonnull Message message, int slotIndex) {
		var description = Parser.readMessageDescription(message);
		var limit = Parser.findRsvpLimitPerSlotInText(description);
		return limit != null && currentRsvpCountForSlot(message, slotIndex) >= limit;
	}

	private long currentRsvpCountForUser(@Nonnull Message message, String userMention) {

		var description = Parser.readMessageDescription(message);

		return description.lines()
			.filter(Parser::inputIsASlot)
			.filter(line -> line.contains(userMention))
			.count();
	}

	private long currentRsvpCountForSlot(@Nonnull Message message, int slotIndexDestination) {

		var slotIndex = 0;
		var description = Parser.readMessageDescription(message);
		var descriptionLines = new LinkedList<>(description.lines().toList());

		for (String line : descriptionLines) {
			if (Parser.inputIsASlot(line)) {

				if (slotIndex == slotIndexDestination) {
					return Parser.readDataInSlot(line).stream().filter(data -> data.contains("@")).count();
				}

				slotIndex++;
			}
		}

		return 0;
	}

}
