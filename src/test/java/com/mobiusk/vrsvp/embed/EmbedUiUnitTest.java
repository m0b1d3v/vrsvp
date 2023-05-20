package com.mobiusk.vrsvp.embed;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.command.SlashCommandInputs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmbedUiUnitTest extends TestBase {

	private static final String USER_MENTION = "@Testing";

	@InjectMocks private EmbedUi embedUi;

	private final SlashCommandInputs inputs = new SlashCommandInputs();

	@BeforeEach
	public void beforeEach() {
		inputs.setBlocks(2);
		inputs.setSlots(3);
		inputs.setDurationInMinutes(4);
		inputs.setStartTimestamp(5);
	}

	@Test
	void buildsEmbedsWithTitles() {

		var embeds = embedUi.build(inputs);

		assertEquals(inputs.getBlocks(), embeds.size());

		for (var embedIndex = 0; embedIndex < embeds.size(); embedIndex++) {
			var embed = embeds.get(embedIndex);
			var expected = String.format("Block %d", embedIndex + 1);
			assertEquals(expected, embed.getTitle());
		}
	}

	@Test
	void buildsEmbedDescriptionsWithSlots() {

		var embeds = embedUi.build(inputs);
		var descriptions = embeds.stream().map(MessageEmbed::getDescription).toList();
		var slots = descriptions.stream().flatMap(String::lines).toList();

		assertEquals(inputs.getBlocks() * inputs.getSlots(), slots.size());

		for (var slotIndex = 0; slotIndex < embeds.size(); slotIndex++) {

			var slot = slots.get(slotIndex);
			var slotTimestamp = inputs.getStartTimestamp() + (inputs.getDurationInMinutes() * 60 * slotIndex);
			var expectedContent = String.format("> #%d, <t:%d:t>", slotIndex + 1, slotTimestamp);

			assertEquals(expectedContent, slot);
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
		var editedEmbeds = embedUi.editEmbedDescriptionFromRSVP(List.of(embed), USER_MENTION, 1);
		var slots = Objects.requireNonNull(editedEmbeds.get(0).getDescription()).split("\n");

		assertEquals("> #1, <t:0:F>, @Testing", slots[0]);
		assertEquals("> #2, <t:1:F>, @Testing", slots[1]);
	}

	// Test utility method(s)

	private String toggleRsvpOnOneEmbedWithOneSlot(String slotValue) {

		var embed = new EmbedBuilder().setDescription(slotValue).build();

		var editedEmbeds = embedUi.editEmbedDescriptionFromRSVP(List.of(embed), USER_MENTION, 0);

		assertEquals(1, editedEmbeds.size());
		return editedEmbeds.get(0).getDescription();
	}

}
