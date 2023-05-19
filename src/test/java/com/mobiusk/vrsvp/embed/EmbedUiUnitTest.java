package com.mobiusk.vrsvp.embed;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.command.SlashCommandInputs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	void buildsEmbedFieldsInlineWithNamesAndValues() {

		var embeds = embedUi.build(inputs);

		var fields = embeds.stream().flatMap(e -> e.getFields().stream()).toList();

		assertEquals(inputs.getBlocks() * inputs.getSlots(), fields.size());

		for (var fieldIndex = 0; fieldIndex < embeds.size(); fieldIndex++) {

			var field = fields.get(fieldIndex);
			var slotTimestamp = inputs.getStartTimestamp() + (inputs.getDurationInMinutes() * 60 * fieldIndex);
			var expectedName = String.format("#%d - <t:%d:t>", fieldIndex + 1, slotTimestamp);

			assertEquals(expectedName, field.getName());
			assertEquals(EmbedUi.EMPTY_SLOT_TEXT, field.getValue());
			assertTrue(field.isInline());
		}
	}

	@Test
	void toggleRsvpForEmptyFieldAddsUserMention() {
		var result = toggleRsvpOnOneEmbedWithOneField(EmbedUi.EMPTY_SLOT_TEXT);
		assertEquals(EmbedUi.SLOT_TEXT_PREFIX + USER_MENTION, result.getValue());
	}

	@Test
	void toggleRsvpForFieldWithExistingMentionRemovesIt() {
		var result = toggleRsvpOnOneEmbedWithOneField(EmbedUi.SLOT_TEXT_PREFIX + USER_MENTION);
		assertEquals(EmbedUi.EMPTY_SLOT_TEXT, result.getValue());
	}

	@Test
	void toggleRsvpForFieldWithExistingMentionsAddsToBottom() {

		var existing = "@Test1\n@Test2";
		var expectation = String.format("%s%s\n%s", EmbedUi.SLOT_TEXT_PREFIX, existing, USER_MENTION);

		var result = toggleRsvpOnOneEmbedWithOneField(EmbedUi.SLOT_TEXT_PREFIX + existing);
		assertEquals(expectation, result.getValue());
	}

	@Test
	void toggleRsvpForFieldWithWithExistingMentionBetweenOtherMentionsRemovesIt() {

		var existing = String.format("@Test1\n%s\n@Test2", USER_MENTION);
		var expectation = String.format("%s@Test1\n@Test2", EmbedUi.SLOT_TEXT_PREFIX);

		var result = toggleRsvpOnOneEmbedWithOneField(EmbedUi.SLOT_TEXT_PREFIX + existing);
		assertEquals(expectation, result.getValue());
	}

	@Test
	void rsvpForMoreThanOneFieldIsAllowed() {

		var embed = new EmbedBuilder()
			.addField(new MessageEmbed.Field("field1", EmbedUi.SLOT_TEXT_PREFIX + USER_MENTION, true))
			.addField(new MessageEmbed.Field("field2", EmbedUi.EMPTY_SLOT_TEXT, true))
			.build();

		var editedEmbeds = embedUi.toggleRsvp(List.of(embed), USER_MENTION, 1);
		var fields = editedEmbeds.get(0).getFields();

		assertEquals(EmbedUi.SLOT_TEXT_PREFIX + USER_MENTION, fields.get(0).getValue());
		assertEquals(EmbedUi.SLOT_TEXT_PREFIX + USER_MENTION, fields.get(1).getValue());
	}

	// Test utility method(s)

	private MessageEmbed.Field toggleRsvpOnOneEmbedWithOneField(String fieldValue) {

		var field = new MessageEmbed.Field("test", fieldValue, true);
		var embed = new EmbedBuilder().addField(field).build();

		var editedEmbeds = embedUi.toggleRsvp(List.of(embed), USER_MENTION, 0);

		assertEquals(1, editedEmbeds.size());
		assertEquals(1, editedEmbeds.get(0).getFields().size());
		return editedEmbeds.get(0).getFields().get(0);
	}

}
