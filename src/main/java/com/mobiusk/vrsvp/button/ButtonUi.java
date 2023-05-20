package com.mobiusk.vrsvp.button;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.LinkedList;
import java.util.List;

public class ButtonUi {

	/**
	 * Builds a list of buttons shown at the bottom of an RSVP form for anyone to use.
	 * <p>
	 * This is cleaner than just trying to shove all the signup buttons together.
	 */
	public List<Button> buildRsvpActionPrompts() {
		return List.of(
			Button.primary(ButtonEnum.RSVP.getId(), ButtonEnum.RSVP.getLabel()),
			Button.secondary(ButtonEnum.EDIT.getId(), ButtonEnum.EDIT.getLabel())
		);
	}

	/**
	 * Builds a list of buttons that correspond to editing either signup message, embeds, or embed fields.
	 */
	public List<Button> buildEditTopLevelActionRow() {
		return List.of(
			Button.primary(ButtonEnum.EDIT_EVENT_DESCRIPTION.getId(), ButtonEnum.EDIT_EVENT_DESCRIPTION.getLabel()),
			Button.primary(ButtonEnum.EDIT_EMBED_DESCRIPTION.getId(), ButtonEnum.EDIT_EMBED_DESCRIPTION.getLabel())
		);
	}

	/**
	 * Builds a list of action rows containing buttons that correspond to available objects in an RSVP message.
	 * <p>
	 * This includes a back button to return to the root of an edit flow.
	 * We can only have five buttons per row, and five rows.
	 */
	public List<ActionRow> buildIndexedButtonActionRowsWithBackButton(String actionId, int slots) {
		var buttons = buildIndexedButtonActionRows(actionId, slots);
		buttons.add(ActionRow.of(Button.secondary(ButtonEnum.EDIT_BACK.getId(), ButtonEnum.EDIT_BACK.getLabel())));
		return buttons;
	}

	/**
	 * Builds a list of action rows containing buttons that correspond to available objects in an RSVP message.
	 * <p>
	 * We can only have five buttons per row, and five rows.
	 */
	public List<ActionRow> buildIndexedButtonActionRows(String actionId, int slots) {

		var buttons = new LinkedList<Button>();

		for (var buttonIndex = 0; buttonIndex < slots; buttonIndex++) {

			var id = String.format("%s:%d", actionId, buttonIndex);
			var label = String.format("#%d", buttonIndex + 1);

			buttons.add(Button.primary(id, label));
		}

		return ActionRow.partitionOf(buttons);
	}

}
