package com.mobiusk.vrsvp.embed;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.input.Inputs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmbedUiUnitTest extends TestBase {

	@InjectMocks private EmbedUi output;

	private final Inputs inputs = new Inputs();

	@BeforeEach
	public void beforeEach() {
		inputs.setBlocks(2);
		inputs.setSlots(3);
		inputs.setDurationInMinutes(4);
		inputs.setStartTimestamp(5);
	}

	@Test
	void buildsEmbedsWithTitles() {

		var embeds = output.build(inputs);

		assertEquals(inputs.getBlocks(), embeds.size());

		for (var embedIndex = 0; embedIndex < embeds.size(); embedIndex++) {
			var embed = embeds.get(embedIndex);
			var expected = String.format("Block %d", embedIndex + 1);
			assertEquals(expected, embed.getTitle());
		}
	}

	@Test
	void buildsEmbedFieldsInlineWithNamesAndValues() {

		var embeds = output.build(inputs);

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

}
