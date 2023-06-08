package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.util.Parser;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

@UtilityClass
public class ButtonUi {

	/**
	 * Builds a list of buttons shown at the bottom of an RSVP form for anyone to use.
	 *
	 * This is cleaner than just trying to shove all the signup buttons together.
	 */
	public static List<Button> buildRsvpActionPrompts() {
		return List.of(
			Button.primary(ButtonEnum.RSVP.getId(), ButtonEnum.RSVP.getLabel()),
			Button.secondary(ButtonEnum.EDIT.getId(), ButtonEnum.EDIT.getLabel()),
			Button.link("https://mobiusk.com/projects/vrsvp", "About")
		);
	}

	/**
	 * Builds buttons that correspond to editing either event description or RSVP button status.
	 */
	public static List<Button> buildEditActionPrompts() {
		return List.of(
			Button.primary(ButtonEnum.EDIT_EVENT_DESCRIPTION.getId(), ButtonEnum.EDIT_EVENT_DESCRIPTION.getLabel()),
			Button.danger(ButtonEnum.EDIT_EVENT_ACTIVE.getId(), ButtonEnum.EDIT_EVENT_ACTIVE.getLabel())
		);
	}

	/**
	 * Builds a list of action rows containing buttons that correspond to available objects in an RSVP message.
	 *
	 * We can only have five buttons per row, and five rows.
	 */
	public static List<ActionRow> buildIndexedButtonActionRows(String actionId, int buttonCount) {

		var buttons = new LinkedList<Button>();

		for (var buttonIndex = 0; buttonIndex < buttonCount; buttonIndex++) {

			var id = String.format("%s:%d", actionId, buttonIndex);
			var label = String.format("#%d", buttonIndex + 1);

			buttons.add(Button.primary(id, label));
		}

		return ActionRow.partitionOf(buttons);
	}

	/**
	 * Toggle (add or remove) a user's mention to the specified slot for the given message.
	 */
	public static String editRsvpFromSignupButtonPress(
		@Nonnull Message message,
		@Nonnull String userMention,
		int slotIndexDestination
	) {

		var slotIndex = 0;

		var description = Parser.readMessageDescription(message);

		var descriptionLines = new LinkedList<>(description.lines().toList());

		for (var lineIndex = 0; lineIndex < descriptionLines.size(); lineIndex++) {

			var line = descriptionLines.get(lineIndex);

			if (Parser.isSlot(line)) {

				if (slotIndex == slotIndexDestination) {
					var editedLine = toggleUserMentionInSlot(line, userMention);
					descriptionLines.set(lineIndex, editedLine);
				}

				slotIndex++;
			}
		}

		return String.join(Parser.SLOT_DELIMITER, descriptionLines);
	}

	private static String toggleUserMentionInSlot(String input, String userMention) {

		var userMentions = Parser.splitSlotText(input);

		var userAlreadySignedUp = userMentions.contains(userMention);
		if (userAlreadySignedUp) {
			userMentions.removeIf(existingMention -> existingMention.equals(userMention));
		} else {
			userMentions.add(userMention);
		}

		return String.join(Parser.SIGNUP_DELIMITER, userMentions);
	}

}
