package com.mobiusk.vrsvp.embed;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.command.SlashCommandInputs;
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
	void buildWithoutRsvpLimitAddendum() {

		var inputs = new SlashCommandInputs();
		inputs.setSlots(2);
		inputs.setDurationInMinutes(5);
		inputs.setStartTimestamp(10);

		var embed = EmbedUi.build(inputs);
		var description = Objects.requireNonNullElse(embed.getDescription(), "");

		var expectation = """
			**New Event**
			
			- Starts <t:10:R> on <t:10:F>
			- Each slot is 5 minutes long
			
			> #1, <t:10:t>
			> #2, <t:310:t>""";

		assertEquals(expectation, description);
	}

	@Test
	void buildWithRsvpLimitAddendum() {

		var inputs = new SlashCommandInputs();
		inputs.setSlots(3);
		inputs.setDurationInMinutes(20);
		inputs.setStartTimestamp(100);
		inputs.setRsvpLimitPerPerson(3);
		inputs.setRsvpLimitPerSlot(2);

		var embed = EmbedUi.build(inputs);
		var description = Objects.requireNonNullElse(embed.getDescription(), "");

		var expectation = """
			**New Event**
			
			- Starts <t:100:R> on <t:100:F>
			- Each slot is 20 minutes long
			- Maximum number of people that can RSVP for a single slot: 2
			- Maximum number of slots a person can RSVP for: 3
			
			> #1, <t:100:t>
			> #2, <t:1300:t>
			> #3, <t:2500:t>""";

		assertEquals(expectation, description);
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
