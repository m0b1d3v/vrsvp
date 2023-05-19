package com.mobiusk.vrsvp.button;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.LinkedList;
import java.util.List;

public class ButtonUi {

	public static final String EDIT = "edit";
	public static final String EDIT_DESCRIPTION = "edit-description";
	public static final String EDIT_EMBED = "edit-embed";
	public static final String EDIT_FIELD_TITLE = "edit-field-title";
	public static final String EDIT_FIELD_VALUE = "edit-field-value";
	public static final String RSVP = "rsvp";
	public static final String SIGNUP = "signup";

	/**
	 * Builds a list of buttons shown at the bottom of an RSVP form for anyone to use.
	 * <p>
	 * This is cleaner than just trying to shove all the signup buttons together.
	 */
	public List<Button> buildRsvpActionPrompts() {
		return List.of(
			Button.primary(RSVP, "RSVP"),
			Button.secondary(EDIT, "Edit")
		);
	}

	/**
	 * Builds a list of buttons that correspond to editing either signup message, embeds, or embed fields.
	 */
	public List<Button> buildEditTopLevelActionRow() {
		return List.of(
			Button.primary(EDIT_DESCRIPTION, "Description"),
			Button.primary(EDIT_EMBED, "Blocks"),
			Button.primary(EDIT_FIELD_TITLE, "Slot Titles"),
			Button.primary(EDIT_FIELD_VALUE, "Slot Values")
		);
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
