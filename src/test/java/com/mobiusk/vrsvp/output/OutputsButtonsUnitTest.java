package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OutputsButtonsUnitTest extends TestBase {

	@InjectMocks
	private OutputsButtons outputsButtons;

	@Test
	void rsvpActionsPromptsBuilt() {

		var buttons = outputsButtons.buildRsvpActionPrompts();

		assertEquals(2, buttons.size());
		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, OutputsButtons.RSVP, "RSVP");
		assertButtonInformation(buttons.get(1), ButtonStyle.SECONDARY, OutputsButtons.EDIT, "Edit");
	}

	@Test
	void slotSignupActionRowsBuiltWithFiveButtonsPerRow() {

		var buttonRows = outputsButtons.buildSlotSignupActionRows(11);

		assertEquals(3, buttonRows.size());
		assertEquals(5, buttonRows.get(0).size());
		assertEquals(5, buttonRows.get(1).size());
		assertEquals(1, buttonRows.get(2).size());
	}

	@Test
	void slotSignupActionRowsButtonsFormedCorrectly() {

		var buttonRows = outputsButtons.buildSlotSignupActionRows(2);
		var buttons = buttonRows.get(0);

		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, "signup:0", "#1");
		assertButtonInformation(buttons.get(1), ButtonStyle.PRIMARY, "signup:1", "#2");
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
