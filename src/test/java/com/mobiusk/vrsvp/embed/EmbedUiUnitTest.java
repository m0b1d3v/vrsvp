package com.mobiusk.vrsvp.embed;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.command.SlashCommandInputs;
import com.mobiusk.vrsvp.util.Parser;
import net.dv8tion.jda.api.EmbedBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class EmbedUiUnitTest extends TestBase {

	private static final String USER_MENTION = "@Testing";

	@InjectMocks private EmbedUi embedUi;

	private final SlashCommandInputs inputs = new SlashCommandInputs();

	@BeforeEach
	public void beforeEach() {
		inputs.setSlots(3);
		inputs.setDurationInMinutes(4);
		inputs.setStartTimestamp(5);
	}

	@Test
	void buildsEmbedDescriptionWithSlots() {

		var embed = embedUi.build(inputs);
		var description = Objects.requireNonNullElse(embed.getDescription(), "");
		var slots = description.lines().filter(Parser::inputIsASlot).toList();

		assertEquals(inputs.getSlots(), slots.size());

		for (var slotIndex = 0; slotIndex < inputs.getSlots(); slotIndex++) {

			var slot = slots.get(slotIndex);
			var slotTimestamp = inputs.getStartTimestamp() + (inputs.getDurationInMinutes() * 60 * slotIndex);
			var expected = String.format("> #%d, <t:%d:t>", slotIndex + 1, slotTimestamp);

			assertEquals(expected, slot);
		}
	}

	@Test
	void toggleRsvpForEmptyFieldAddsUserMention() {
		var result = toggleRsvpOnOneEmbedWithOneSlot("> #1, <t:5:t>");
		assertEquals("> #1, <t:5:t>, @Testing", result);
	}

	@Test
	void toggleRsvpForSlotWithExistingMentionRemovesIt() {
		var result = toggleRsvpOnOneEmbedWithOneSlot("> #1, <t:5:t>, @Testing");
		assertEquals("> #1, <t:5:t>", result);
	}

	@Test
	void toggleRsvpForSlotWithExistingMentionsAddsToEnd() {
		var result = toggleRsvpOnOneEmbedWithOneSlot("> #1, <t:5:t>, @Test1, @Test2");
		assertEquals("> #1, <t:5:t>, @Test1, @Test2, @Testing", result);
	}

	@Test
	void toggleRsvpForSlotWithWithExistingMentionBetweenOtherMentionsRemovesIt() {
		var result = toggleRsvpOnOneEmbedWithOneSlot("> #1, <t:5:t>, @Test1, @Testing, @Test2");
		assertEquals("> #1, <t:5:t>, @Test1, @Test2", result);
	}

	@Test
	void rsvpForMoreThanOneFieldIsAllowed() {

		var embed = new EmbedBuilder().setDescription("> #1, <t:0:F>, @Testing\n> #2, <t:1:F>").build();
		when(message.getEmbeds()).thenReturn(List.of(embed));

		var editedEmbed = embedUi.editEmbedDescriptionFromRSVP(message, USER_MENTION, 1);
		var slots = Objects.requireNonNull(editedEmbed.getDescription()).split("\n");

		assertEquals("> #1, <t:0:F>, @Testing", slots[0]);
		assertEquals("> #2, <t:1:F>, @Testing", slots[1]);
	}

	// Test utility method(s)

	private String toggleRsvpOnOneEmbedWithOneSlot(String slotValue) {

		var embed = new EmbedBuilder().setDescription(slotValue).build();
		when(message.getEmbeds()).thenReturn(List.of(embed));

		var editedEmbed = embedUi.editEmbedDescriptionFromRSVP(message, USER_MENTION, 0);

		return editedEmbed.getDescription();
	}

}
