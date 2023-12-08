package dev.m0b1.vrsvp.button;

import dev.m0b1.vrsvp.logging.LogData;
import dev.m0b1.vrsvp.logging.ServiceLog;
import dev.m0b1.vrsvp.modal.ModalEnum;
import dev.m0b1.vrsvp.modal.ModalUi;
import dev.m0b1.vrsvp.util.Parser;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;

@RequiredArgsConstructor
@Service
@Slf4j
public class ButtonReply {

	private final ServiceLog serviceLog;

	/**
	 * Replies with an ephemeral list of buttons admins can click to start editing part of an RSVP form.
	 */
	public void editInterest(@Nonnull ButtonInteractionEvent event) {

		var buttons = ButtonUi.buildEditActionPrompts();

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
		var modal = ModalUi.editText(ModalEnum.EVENT_DESCRIPTION, currentText);

		event.replyModal(modal).queue();
	}

	/**
	 * Replies with an ephemeral list of buttons users can click to toggle RSVP state for slots.
	 */
	public void rsvpInterest(@Nonnull ButtonInteractionEvent event) {

		var description = Parser.readMessageDescription(event.getMessage());
		var slots = Parser.countSlotsInText(description);
		var buttonRows = ButtonUi.buildIndexedButtonActionRows(ButtonEnum.RSVP.getId(), slots);

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

		var rsvpButton = message.getButtonById(ButtonEnum.RSVP.getId());
		if (rsvpButton == null || rsvpButton.isDisabled()) {

			serviceLog.run(LogData.builder()
				.level(Level.WARN)
				.message("RSVP declined for disabled event")
				.event(event)
			);

			event.editMessage("RSVP has been disabled for this event.").queue();
			return;
		}

		var userMention = event.getUser().getAsMention();
		var editedDescription = ButtonUi.editRsvpFromSignupButtonPress(message, userMention, slotIndex);

		if (rsvpLimitsExceeded(message, editedDescription, userMention, slotIndex)) {

			serviceLog.run(LogData.builder()
				.level(Level.INFO)
				.message("RSVP declined for signup limit")
				.event(event)
			);

			var errorMessage = STR."Signup limit exceeded, cannot RSVP for slot #\{slotIndex + 1}";
			event.editMessage(errorMessage).queue();
			return;
		}

		try {

			message.editMessageEmbeds(Collections.emptyList())
				.setContent(editedDescription)
				.queue();

			var reply = STR."RSVP state toggled for slot #\{slotIndex + 1}";
			event.editMessage(reply).queue();

		} catch (IllegalArgumentException e) {

			var reply = """
				VRSVP has hit the Discord message length limit when trying to RSVP.
				Please contact the event runner to see if they can free up some space in the event description.
				""";

			event.editMessage(reply).queue();
		}
	}

	/**
	 * Generic ephemeral reply in response to a button press, mostly for validation errors or development feedback.
	 */
	public void ephemeral(@Nonnull ButtonInteractionEvent event, String message) {
		event.reply(message).setEphemeral(true).queue();
	}

	private boolean rsvpLimitsExceeded(
		@Nonnull Message message,
		String editedDescription,
		String userMention,
		int slotIndex
	) {

		var originalDescription = Parser.readMessageDescription(message);

		return editedDescription.length() > originalDescription.length()
			&& (rsvpLimitPerPersonExceeded(message, userMention) || rsvpLimitPerSlotExceeded(message, slotIndex));
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
			.filter(Parser::isSlot)
			.filter(line -> line.contains(userMention))
			.count();
	}

	private long currentRsvpCountForSlot(@Nonnull Message message, int slotIndexDestination) {

		var slotIndex = 0;
		var description = Parser.readMessageDescription(message);
		var descriptionLines = new LinkedList<>(description.lines().toList());

		var count = 0L;
		for (String line : descriptionLines) {
			if (Parser.isSlot(line)) {

				if (slotIndex == slotIndexDestination) {
					count = Parser.splitSlotText(line).stream().filter(data -> data.contains("@")).count();
					break;
				}

				slotIndex++;
			}
		}

		return count;
	}

}
