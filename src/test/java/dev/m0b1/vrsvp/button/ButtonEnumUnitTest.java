package dev.m0b1.vrsvp.button;

import dev.m0b1.vrsvp.TestBase;
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
