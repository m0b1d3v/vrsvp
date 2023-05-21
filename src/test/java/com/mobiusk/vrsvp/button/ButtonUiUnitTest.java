package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ButtonUiUnitTest extends TestBase {

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(ButtonUi.class);
	}

	@Test
	void buildRsvpActionPrompts() {

		var buttons = ButtonUi.buildRsvpActionPrompts();

		assertEquals(3, buttons.size());
		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, ButtonEnum.RSVP);
		assertButtonInformation(buttons.get(1), ButtonStyle.SECONDARY, ButtonEnum.EDIT);

		var aboutLink = buttons.get(2);
		assertEquals(ButtonStyle.LINK, aboutLink.getStyle());
		assertEquals("https://mobiusk.com/code/vrsvp", aboutLink.getUrl());
		assertEquals("About", aboutLink.getLabel());
	}

	@Test
	void buildEditActionPrompts() {

		var buttons = ButtonUi.buildEditActionPrompts();

		assertEquals(2, buttons.size());
		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, ButtonEnum.EDIT_EVENT_DESCRIPTION);
		assertButtonInformation(buttons.get(1), ButtonStyle.DANGER, ButtonEnum.EDIT_EVENT_ACTIVE);
	}

	@Test
	void buildIndexedButtonActionRowsPartitionCorrectly() {

		var buttonRows = ButtonUi.buildIndexedButtonActionRows(ButtonEnum.RSVP.getId(), 11);

		assertEquals(3, buttonRows.size());
		assertEquals(5, buttonRows.get(0).getButtons().size());
		assertEquals(5, buttonRows.get(1).getButtons().size());
		assertEquals(1, buttonRows.get(2).getButtons().size());
	}

	@Test
	void buildIndexedButtonActionRowsFormButtonsCorrectly() {

		var buttonRows = ButtonUi.buildIndexedButtonActionRows(ButtonEnum.RSVP.getId(), 2);
		var buttons = buttonRows.get(0).getButtons();

		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, "rsvp:0", "#1");
		assertButtonInformation(buttons.get(1), ButtonStyle.PRIMARY, "rsvp:1", "#2");
	}

	// Test utility method(s)

	private void assertButtonInformation(Button button, ButtonStyle buttonStyle, ButtonEnum buttonEnum) {
		assertButtonInformation(button, buttonStyle, buttonEnum.getId(), button.getLabel());
	}

	private void assertButtonInformation(Button button, ButtonStyle buttonStyle, String id, String label) {

		assertEquals(buttonStyle, button.getStyle());
		assertEquals(id, button.getId());
		assertEquals(label, button.getLabel());

		assertEquals(5, button.getMaxPerRow());

		assertNull(button.getUrl());
		assertNull(button.getEmoji());
	}

}
