package com.mobiusk.vrsvp.util;

import com.mobiusk.vrsvp.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatterUnitTest extends TestBase {

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(Formatter.class);
	}

	@Test
	void outputFormatted() {
		assertEquals("---\nTesting\n---", Formatter.replies("Testing"));
	}

}
