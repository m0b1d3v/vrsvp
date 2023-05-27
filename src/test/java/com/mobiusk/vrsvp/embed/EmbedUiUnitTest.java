package com.mobiusk.vrsvp.embed;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.EmbedBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class EmbedUiUnitTest extends TestBase {

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(EmbedUi.class);
	}

	@Test
	void editEmbedDescriptionFromAdmin() {

		var embed = new EmbedBuilder().setTitle("Original Title").setDescription("Original Description").build();
		when(message.getEmbeds()).thenReturn(List.of(embed));

		var result = EmbedUi.editEmbedDescriptionFromAdmin(message, "Testing");

		assertEquals(embed.getTitle(), result.getTitle());
		assertEquals("Testing", result.getDescription());
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

		var editedEmbed = EmbedUi.editEmbedDescriptionFromRSVP(message, "@Testing", 1);
		var slots = Objects.requireNonNull(editedEmbed.getDescription()).split("\n");

		assertEquals("> #1, <t:0:F>, @Testing", slots[0]);
		assertEquals("> #2, <t:1:F>, @Testing", slots[1]);
	}

	// Test utility method(s)

	private String toggleRsvp(String slotValue) {

		var embed = new EmbedBuilder().setDescription(slotValue).build();
		when(message.getEmbeds()).thenReturn(List.of(embed));

		var editedEmbed = EmbedUi.editEmbedDescriptionFromRSVP(message, "@Testing", 0);

		return editedEmbed.getDescription();
	}

}
