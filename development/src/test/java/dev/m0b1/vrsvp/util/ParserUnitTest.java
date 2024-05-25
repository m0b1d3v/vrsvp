package dev.m0b1.vrsvp.util;

import dev.m0b1.vrsvp.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ParserUnitTest extends TestBase {

	@BeforeEach
	public void beforeEach() {
		when(message.getContentRaw()).thenReturn("non-embed content");
		when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
	}

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(Parser.class);
	}

	@Test
	void readsContentRawIfMessageEmbedsNotFound() {
		when(message.getEmbeds()).thenReturn(null);
		assertEquals("non-embed content", Parser.readMessageDescription(message));
	}

	@Test
	void readsContentRawIfMessageEmbedsEmpty() {
		when(message.getEmbeds()).thenReturn(Collections.emptyList());
		assertEquals("non-embed content", Parser.readMessageDescription(message));
	}

	@Test
	void readNullMessageDescriptionDefaultsToEmptyString() {
		when(messageEmbed.getDescription()).thenReturn(null);
		assertEquals("", Parser.readMessageDescription(message));
	}

	@Test
	void readMessageDescription() {
		when(messageEmbed.getDescription()).thenReturn("Testing");
		assertEquals("Testing", Parser.readMessageDescription(message));
	}

	@Test
	void isSlot() {
		List.of("> #", "> #1", "> #2").forEach(line -> assertTrue(Parser.isSlot(line)));
		List.of("> ", " #", "").forEach(line -> assertFalse(Parser.isSlot(line)));
	}

	@Test
	void countSlotsInText() {

		var input = """
			Header
			> Ignored
			#1 Still ignored
			> #1

			> #2""";

		assertEquals(2, Parser.countSlotsInText(input));
	}

	@Test
	void splitSlotTextIsEditable() {

		var result = Parser.splitSlotText("a, b, c");
		result.remove(1);

		assertEquals(2, result.size());
		assertEquals("a", result.get(0));
		assertEquals("c", result.get(1));
	}

	@Test
	void findRsvpLimitPerPersonInTextWithoutMatch() {
		assertNull(Parser.findRsvpLimitPerPersonInText(null));
		assertNull(Parser.findRsvpLimitPerPersonInText(" Maximum number of slots: 2"));
	}

	@Test
	void findRsvpLimitPerPersonInText() {

		var input = """

			Arbitrary prefix Maximum number of slots a person can RSVP for: 3 Suffix
			More lines""";


		assertEquals(3, Parser.findRsvpLimitPerPersonInText(input));
	}

	@Test
	void findRsvpLimitPerPersonInTextOnlyTakesFirstResult() {

		var input = """
			Maximum number of slots a person can RSVP for: 3
			Maximum number of slots a person can RSVP for: 1""";


		assertEquals(3, Parser.findRsvpLimitPerPersonInText(input));
	}

	@Test
	void findRsvpLimitPerSlotInTextWithoutMatch() {
		assertNull(Parser.findRsvpLimitPerSlotInText(null));
		assertNull(Parser.findRsvpLimitPerSlotInText(" Maximum number of people: 2"));
	}

	@Test
	void findRsvpLimitPerSlotInText() {

		var input = """
			   Maximum number of people that can RSVP for a single slot: 2 (until further notice)
			More lines""";

		assertEquals(2, Parser.findRsvpLimitPerSlotInText(input));
	}

	@Test
	void findRsvpLimitPerSlotInTextOnlyTakesFirstResult() {

		var input = """
			Maximum number of people that can RSVP for a single slot: 2
			Maximum number of people that can RSVP for a single slot: 1""";

		assertEquals(2, Parser.findRsvpLimitPerSlotInText(input));
	}

}
