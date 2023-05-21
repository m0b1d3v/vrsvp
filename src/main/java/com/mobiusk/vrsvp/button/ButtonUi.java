package com.mobiusk.vrsvp.button;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

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
			Button.link("https://mobiusk.com/code/vrsvp", "About")
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

}
