package dev.m0b1.vrsvp.button;

import dev.m0b1.vrsvp.util.Parser;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

@UtilityClass
public class ButtonUi {

	public static final Emoji EMOJI_RSVP = Emoji.fromUnicode("\uD83D\uDCDD"); // :pencil:
	public static final Emoji EMOJI_EDIT = Emoji.fromUnicode("\uD83D\uDD12"); // :lock:

	/**
	 * Builds a list of buttons shown at the bottom of an RSVP form for anyone to use.
	 *
	 * This is cleaner than just trying to shove all the signup buttons together.
	 */
	public static List<Button> buildRsvpActionPrompts() {
		return List.of(
			Button.primary(ButtonEnum.RSVP.getId(), ButtonEnum.RSVP.getLabel()).withEmoji(EMOJI_RSVP),
			Button.secondary(ButtonEnum.EDIT.getId(), ButtonEnum.EDIT.getLabel()).withEmoji(EMOJI_EDIT),
			Button.link(ButtonEnum.ABOUT.getId(), ButtonEnum.ABOUT.getLabel())
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

		Predicate<String> mentionMatchPredicate = (String existingMention) -> existingMention.contains(userMention);

		if (userMentions.stream().anyMatch(mentionMatchPredicate)) {
			userMentions.removeIf(mentionMatchPredicate);
		} else {
			userMentions.add(userMention);
		}

		return String.join(Parser.SIGNUP_DELIMITER, userMentions);
	}

}
