package dev.m0b1.vrsvp.modal;

import dev.m0b1.vrsvp.TestBase;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.internal.components.label.LabelImpl;
import net.dv8tion.jda.internal.components.textinput.TextInputImpl;
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
		assertEquals(LabelImpl.class, modal.getComponents().getFirst().getClass());
		assertEquals(TextInputImpl.class, modal.getComponents().getFirst().asLabel().getChild().getClass());
	}

	@Test
	void modalBuildsTextInputCorrectly() {

		var modal = ModalUi.editText(ModalEnum.EVENT_DESCRIPTION, "Testing");

		var label = getLabelComponent(modal);
		var textInput = getTextInputComponent(modal);
		assertEquals(ModalEnum.EVENT_DESCRIPTION.getId(), textInput.getCustomId());
		assertEquals(ModalEnum.EVENT_DESCRIPTION.getLabel(), label.getLabel());
		assertEquals(ModalEnum.EVENT_DESCRIPTION.getPlaceholder(), textInput.getPlaceHolder());
		assertEquals(TextInputStyle.PARAGRAPH, textInput.getStyle());
		assertEquals(Message.MAX_CONTENT_LENGTH, textInput.getMaxLength());
		assertEquals("Testing", textInput.getValue());
	}

	@Test
	void modalBuildsCorrectly() {

		var modal = ModalUi.editText(ModalEnum.EVENT_DESCRIPTION, "Testing");

		assertEquals(ModalEnum.EVENT_DESCRIPTION.getId(), modal.getId());
		assertEquals("VRSVP", modal.getTitle());
	}

	private Label getLabelComponent(Modal modal) {
		return modal
			.getComponents()
			.getFirst()
			.asLabel();
	}

	private TextInput getTextInputComponent(Modal modal) {
		return getLabelComponent(modal)
			.getChild()
			.asTextInput();
	}

}
