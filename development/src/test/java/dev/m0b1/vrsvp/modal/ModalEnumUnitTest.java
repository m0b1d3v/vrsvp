package dev.m0b1.vrsvp.modal;

import dev.m0b1.vrsvp.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModalEnumUnitTest extends TestBase {

	@Test
	void modalEnumCanBeFoundById() {
		for (var modalEnum : ModalEnum.values()) {
			assertEquals(modalEnum, ModalEnum.getById(modalEnum.getId()));
		}
	}

	@Test
	void modalEnumDefaultsToUnknownForBadId() {
		assertEquals(ModalEnum.UNKNOWN, ModalEnum.getById("testing"));
	}

}
