package dev.m0b1.vrsvp.modal;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.modals.Modal;

@UtilityClass
public class ModalUi {

	public static final String MODAL_PLACEHOLDER = "Title, important timestamps, and slots are good to have here.";

	/**
	 * Creates a modal that allows exactly one existing string to be edited in paragraph format.
	 */
	public static Modal editText(ModalEnum modalEnum, String existingText) {

		var id = modalEnum.getId();
		var textInput = buildTextInput(id, modalEnum, existingText);

		return Modal.create(id, "VRSVP")
			.addComponents(Label.of(modalEnum.getLabel(), textInput))
			.build();
	}

	private static TextInput buildTextInput(String id, ModalEnum modalEnum, String existingText) {
		return TextInput.create(id, TextInputStyle.PARAGRAPH)
			.setMaxLength(Message.MAX_CONTENT_LENGTH)
			.setPlaceholder(modalEnum.getPlaceholder())
			.setValue(existingText)
			.build();
	}

}
