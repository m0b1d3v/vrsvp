package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.modal.ModalEnum;
import com.mobiusk.vrsvp.embed.EmbedUi;
import com.mobiusk.vrsvp.modal.ModalUi;
import com.mobiusk.vrsvp.util.Formatter;
import com.mobiusk.vrsvp.util.Parser;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
	 * Toggle the disabled state of the RSVP button on a message.
	 */
	public void editToggleRsvpActive(@Nonnull ButtonInteractionEvent event, @Nonnull Message message) {

		var rsvpButton = message.getButtonById(ButtonEnum.RSVP.getId());
		if (rsvpButton == null) {
			var reply = Formatter.replies("Could not toggle RSVP button");
			event.editMessage(reply).queue();
			return;
		}

		rsvpButton = rsvpButton.withDisabled( ! rsvpButton.isDisabled());

		var buttons = new LinkedList<>(message.getButtons());
		buttons.set(0, rsvpButton);

		message.editMessageComponents(ActionRow.of(buttons)).queue();

		var reply = Formatter.replies("RSVP button toggled");
		event.editMessage(reply).queue();
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

		var userMention = event.getUser().getAsMention();
		var result = embedUi.editEmbedDescriptionFromRSVP(message, userMention, slotIndex);

		if (result.isUserAddedToSlot()
			&& (rsvpLimitPerPersonExceeded(message, userMention) || rsvpLimitPerSlotExceeded(message, slotIndex))
		) {
			var errorMessage = Formatter.replies(String.format("Signup limit exceeded, cannot RSVP for slot #%d", slotIndex + 1));
			event.editMessage(errorMessage).queue();
			return;
		}

		message.editMessageEmbeds(result.getMessageEmbeds()).queue();

		var reply = Formatter.replies(String.format("RSVP state toggled for slot #%d", slotIndex + 1));
		event.editMessage(reply).queue();
	}

	/**
	 * Generic ephemeral reply in response to a button press, mostly for validation errors or development feedback.
	 */
	public void ephemeral(@Nonnull ButtonInteractionEvent event, String message) {
		event.reply(message).setEphemeral(true).queue();
	}

	private boolean rsvpLimitPerPersonExceeded(@Nonnull Message message, String userMention) {
		var description = message.getContentDisplay();
		var limit = Parser.findRsvpLimitPerPersonInText(description);
		return limit != null && currentRsvpCountForUser(message, userMention) >= limit;
	}

	private boolean rsvpLimitPerSlotExceeded(@Nonnull Message message, int slotIndex) {
		var description = message.getContentDisplay();
		var limit = Parser.findRsvpLimitPerSlotInText(description);
		return limit != null && currentRsvpCountForSlot(message, slotIndex) >= limit;
	}

	private long currentRsvpCountForUser(@Nonnull Message message, String userMention) {
		return message.getEmbeds().stream()
			.map(MessageEmbed::getDescription)
			.filter(Objects::nonNull)
			.flatMap(String::lines)
			.filter(Parser::inputIsASlot)
			.filter(line -> line.contains(userMention))
			.count();
	}

	private long currentRsvpCountForSlot(@Nonnull Message message, int slotIndexDestination) {

		var slotIndex = 0;
		for (var embed : message.getEmbeds()) {

			var description = Objects.requireNonNullElse(embed.getDescription(), "");
			var descriptionLines = new LinkedList<>(description.lines().toList());

			for (String line : descriptionLines) {
				if (Parser.inputIsASlot(line)) {

					if (slotIndex == slotIndexDestination) {
						return Parser.readDataInSlot(line).stream().filter(data -> data.contains("@")).count();
					}

					slotIndex++;
				}
			}
		}

		return 0;
	}

}
