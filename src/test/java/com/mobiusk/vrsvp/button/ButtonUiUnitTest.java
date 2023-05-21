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

		assertEquals(3, buttons.size());
		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, ButtonEnum.RSVP.getId(), ButtonEnum.RSVP.getLabel());
		assertButtonInformation(buttons.get(1), ButtonStyle.SECONDARY, ButtonEnum.EDIT.getId(), ButtonEnum.EDIT.getLabel());

		var aboutLink = buttons.get(2);
		assertEquals(ButtonStyle.LINK, aboutLink.getStyle());
		assertEquals("https://mobiusk.com/code/vrsvp", aboutLink.getUrl());
		assertEquals("About", aboutLink.getLabel());
	}

	@Test
	void editActionsPromptsBuilt() {

		var buttons = buttonUi.buildEditActionPrompts();

		assertEquals(2, buttons.size());
		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, ButtonEnum.EDIT_EVENT_DESCRIPTION.getId(), ButtonEnum.EDIT_EVENT_DESCRIPTION.getLabel());
		assertButtonInformation(buttons.get(1), ButtonStyle.DANGER, ButtonEnum.EDIT_EVENT_ACTIVE.getId(), ButtonEnum.EDIT_EVENT_ACTIVE.getLabel());
	}

	@Test
	void rsvpActionRowsBuiltWithFiveButtonsPerRow() {

		var buttonRows = buttonUi.buildIndexedButtonActionRows(ButtonEnum.RSVP.getId(), 11);

		assertEquals(3, buttonRows.size());
		assertEquals(5, buttonRows.get(0).getButtons().size());
		assertEquals(5, buttonRows.get(1).getButtons().size());
		assertEquals(1, buttonRows.get(2).getButtons().size());
	}

	@Test
	void rsvpActionRowsButtonsFormedCorrectly() {

		var buttonRows = buttonUi.buildIndexedButtonActionRows(ButtonEnum.RSVP.getId(), 2);
		var buttons = buttonRows.get(0);

		assertButtonInformation(buttons.getButtons().get(0), ButtonStyle.PRIMARY, "rsvp:0", "#1");
		assertButtonInformation(buttons.getButtons().get(1), ButtonStyle.PRIMARY, "rsvp:1", "#2");
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
