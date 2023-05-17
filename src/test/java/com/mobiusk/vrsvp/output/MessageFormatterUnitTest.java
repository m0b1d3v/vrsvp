package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageFormatterUnitTest extends TestBase {

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(MessageFormatter.class);
	}

	@Test
	void outputFormatted() {
		assertEquals("---\nTesting\n---", MessageFormatter.output("Testing"));
	}

}
