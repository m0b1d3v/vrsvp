package com.mobiusk.vrsvp.modal;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.internal.interactions.component.TextInputImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModalUiUnitTest extends TestBase {

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(ModalUi.class);
	}

	@Test
	void modalOnlyMakesOneTextInput() {

		var modal = ModalUi.editText(ModalEnum.EVENT_DESCRIPTION, "Testing");

		assertEquals(1, modal.getComponents().size());
		assertEquals(1, modal.getComponents().get(0).getActionComponents().size());
		assertEquals(TextInputImpl.class, modal.getComponents().get(0).getActionComponents().get(0).getClass());
	}

	@Test
	void modalBuildsTextInputCorrectly() {

		var modal = ModalUi.editText(ModalEnum.EVENT_DESCRIPTION, "Testing");

		var textInput = (TextInputImpl) modal.getComponents().get(0).getActionComponents().get(0);
		assertEquals(ModalEnum.EVENT_DESCRIPTION.getId(), textInput.getId());
		assertEquals(ModalEnum.EVENT_DESCRIPTION.getLabel(), textInput.getLabel());
		assertEquals(ModalEnum.EVENT_DESCRIPTION.getPlaceholder(), textInput.getPlaceHolder());
		assertEquals(TextInputStyle.PARAGRAPH, textInput.getStyle());
		assertEquals("Testing", textInput.getValue());
	}

	@Test
	void modalBuildsCorrectly() {

		var modal = ModalUi.editText(ModalEnum.EVENT_DESCRIPTION, "Testing");

		assertEquals(ModalEnum.EVENT_DESCRIPTION.getId(), modal.getId());
		assertEquals("VRSVP", modal.getTitle());
	}

}
