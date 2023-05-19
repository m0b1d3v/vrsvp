package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ButtonUiUnitTest extends TestBase {

	@InjectMocks private ButtonUi buttonUi;

	@Test
	void rsvpActionsPromptsBuilt() {

		var buttons = buttonUi.buildRsvpActionPrompts();

		assertEquals(2, buttons.size());
		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, ButtonUi.RSVP, "RSVP");
		assertButtonInformation(buttons.get(1), ButtonStyle.SECONDARY, ButtonUi.EDIT, "Edit");
	}

	@Test
	void editTopLevelPromptsBuilt() {

		var buttons = buttonUi.buildEditTopLevelActionRow();

		assertEquals(4, buttons.size());
		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, ButtonUi.EDIT_DESCRIPTION, "Description");
		assertButtonInformation(buttons.get(1), ButtonStyle.PRIMARY, ButtonUi.EDIT_EMBED, "Blocks");
		assertButtonInformation(buttons.get(2), ButtonStyle.PRIMARY, ButtonUi.EDIT_FIELD_TITLE, "Slot Titles");
		assertButtonInformation(buttons.get(3), ButtonStyle.PRIMARY, ButtonUi.EDIT_FIELD_VALUE, "Slot Values");
	}

	@Test
	void slotSignupActionRowsBuiltWithFiveButtonsPerRow() {

		var buttonRows = buttonUi.buildIndexedButtonActionRows(ButtonUi.SIGNUP, 11);

		assertEquals(3, buttonRows.size());
		assertEquals(5, buttonRows.get(0).getButtons().size());
		assertEquals(5, buttonRows.get(1).getButtons().size());
		assertEquals(1, buttonRows.get(2).getButtons().size());
	}

	@Test
	void slotSignupActionRowsButtonsFormedCorrectly() {

		var buttonRows = buttonUi.buildIndexedButtonActionRows(ButtonUi.SIGNUP, 2);
		var buttons = buttonRows.get(0);

		assertButtonInformation(buttons.getButtons().get(0), ButtonStyle.PRIMARY, "signup:0", "#1");
		assertButtonInformation(buttons.getButtons().get(1), ButtonStyle.PRIMARY, "signup:1", "#2");
	}

	// Test utility method(s)

	private void assertButtonInformation(Button button, ButtonStyle buttonStyle, String id, String label) {

		assertEquals(buttonStyle, button.getStyle());
		assertEquals(id, button.getId());
		assertEquals(label, button.getLabel());

		assertEquals(5, button.getMaxPerRow());

		assertNull(button.getUrl());
		assertNull(button.getEmoji());
	}

}
