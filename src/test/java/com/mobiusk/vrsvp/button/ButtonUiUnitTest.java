package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class ButtonUiUnitTest extends TestBase {

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(ButtonUi.class);
	}

	@Test
	void buildRsvpActionPrompts() {

		var buttons = ButtonUi.buildRsvpActionPrompts();

		assertEquals(3, buttons.size());
		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, ButtonEnum.RSVP, ButtonUi.EMOJI_RSVP);
		assertButtonInformation(buttons.get(1), ButtonStyle.SECONDARY, ButtonEnum.EDIT, ButtonUi.EMOJI_EDIT);
		assertButtonInformation(buttons.get(2), ButtonStyle.LINK, ButtonEnum.ABOUT, null);
	}

	@Test
	void buildEditActionPrompts() {

		var buttons = ButtonUi.buildEditActionPrompts();

		assertEquals(2, buttons.size());

		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, ButtonEnum.EDIT_EVENT_DESCRIPTION, null);
		assertButtonInformation(buttons.get(1), ButtonStyle.DANGER, ButtonEnum.EDIT_EVENT_ACTIVE, null);
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

		assertButtonInformation(buttons.get(0), ButtonStyle.PRIMARY, "rsvp:0", "#1", null);
		assertButtonInformation(buttons.get(1), ButtonStyle.PRIMARY, "rsvp:1", "#2", null);
	}

	@Test
	void editEmbedDescriptionFromRsvpAddsUserMention() {
		var result = toggleRsvp("> #1, <t:5:t>");
		assertEquals("> #1, <t:5:t>, @Testing", result);
	}

	@Test
	void editEmbedDescriptionFromRsvpWithExistingMentionRemovesIt() {
		var result = toggleRsvp("> #1, <t:5:t>, @Testing");
		assertEquals("> #1, <t:5:t>", result);
	}

	@Test
	void editEmbedDescriptionFromRsvpWithExistingMentionsAddsToEnd() {
		var result = toggleRsvp("> #1, <t:5:t>, @Test1, @Test2");
		assertEquals("> #1, <t:5:t>, @Test1, @Test2, @Testing", result);
	}

	@Test
	void editEmbedDescriptionFromRsvpWithWithExistingMentionBetweenOtherMentionsRemovesIt() {
		var result = toggleRsvp("> #1, <t:5:t>, @Test1, @Testing, @Test2");
		assertEquals("> #1, <t:5:t>, @Test1, @Test2", result);
	}

	@Test
	void editEmbedDescriptionFromRsvpForMoreThanOneFieldIsAllowed() {

		var embed = new EmbedBuilder().setDescription("> #1, <t:0:F>, @Testing\n> #2, <t:1:F>").build();
		when(message.getEmbeds()).thenReturn(List.of(embed));

		var editedDescription = ButtonUi.editRsvpFromSignupButtonPress(message, "@Testing", 1);
		var slots = editedDescription.split("\n");

		assertEquals("> #1, <t:0:F>, @Testing", slots[0]);
		assertEquals("> #2, <t:1:F>, @Testing", slots[1]);
	}

	// Test utility method(s)

	private void assertButtonInformation(Button button, ButtonStyle buttonStyle, ButtonEnum buttonEnum, Emoji emoji) {
		assertButtonInformation(button, buttonStyle, buttonEnum.getId(), buttonEnum.getLabel(), emoji);
	}

	private void assertButtonInformation(
		Button button,
		ButtonStyle buttonStyle,
		String id,
		String label,
		Emoji emoji
	) {

		assertEquals(buttonStyle, button.getStyle());
		assertEquals(label, button.getLabel());
		assertEquals(emoji, button.getEmoji());

		if (ButtonStyle.LINK.equals(buttonStyle)) {
			assertNull(button.getId());
			assertEquals(id, button.getUrl());
		} else {
			assertEquals(id, button.getId());
			assertNull(button.getUrl());
		}

		assertEquals(5, button.getMaxPerRow());
	}

	private String toggleRsvp(String slotValue) {

		var embed = new EmbedBuilder().setDescription(slotValue).build();
		when(message.getEmbeds()).thenReturn(List.of(embed));

		return ButtonUi.editRsvpFromSignupButtonPress(message, "@Testing", 0);
	}

}
