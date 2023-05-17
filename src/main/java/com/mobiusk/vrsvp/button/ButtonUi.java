package com.mobiusk.vrsvp.button;

import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.LinkedList;
import java.util.List;

public class ButtonUi {

	public static final String EDIT = "edit";
	public static final String RSVP = "rsvp";
	public static final String SIGNUP = "signup";

	/**
	 * Builds a list of buttons shown at the bottom of an RSVP form for anyone to use.
	 * <p>
	 * This is cleaner than just trying to shove all the signup buttons together.
	 */
	public List<Button> buildRsvpActionPrompts() {
		return List.of(
			Button.of(ButtonStyle.PRIMARY, RSVP, "RSVP"),
			Button.of(ButtonStyle.SECONDARY, EDIT, "Edit")
		);
	}

	/**
	 * Builds a list (of lists) of buttons that correspond to available slots in an RSVP message.
	 * <p>
	 * We can only have five buttons per row, and five rows, hence the nested lists.
	 */
	public List<List<Button>> buildSlotSignupActionRows(int slots) {

		var buttons = new LinkedList<Button>();
		for (var buttonIndex = 0; buttonIndex < slots; buttonIndex++) {

			var id = String.format("%s:%d", SIGNUP, buttonIndex);
			var label = String.format("#%d", buttonIndex + 1);

			buttons.add(Button.of(ButtonStyle.PRIMARY, id, label));
		}

		return splitButtonListForActionRow(buttons);
	}

	private List<List<Button>> splitButtonListForActionRow(List<Button> input) {

		var output = new LinkedList<List<Button>>();
		for (var inputIndex = 0; inputIndex < input.size(); inputIndex += 5) {
			var subListEndIndex = Math.min(input.size(), inputIndex + 5);
			var subList = input.subList(inputIndex, subListEndIndex);
			output.add(subList);
		}

		return output;
	}

}
