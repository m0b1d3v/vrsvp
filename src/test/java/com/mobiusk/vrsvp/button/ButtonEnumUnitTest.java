package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ButtonEnumUnitTest extends TestBase {

	@Test
	void buttonEnumCanBeFoundById() {
		for (var buttonEnum : ButtonEnum.values()) {
			assertEquals(buttonEnum, ButtonEnum.getById(buttonEnum.getId()));
		}
	}

	@Test
	void buttonEnumDefaultsToUnknownForBadId() {
		assertEquals(ButtonEnum.UNKNOWN, ButtonEnum.getById("testing"));
	}

}
