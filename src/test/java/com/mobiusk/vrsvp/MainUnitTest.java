package com.mobiusk.vrsvp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainUnitTest extends TestBase {

	@Test
	void mainConstructed() {
		assertDoesNotThrow(Main::new);
	}

}
