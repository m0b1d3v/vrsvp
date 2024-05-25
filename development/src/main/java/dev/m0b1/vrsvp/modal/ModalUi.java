package dev.m0b1.vrsvp.modal;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@UtilityClass
public class ModalUi {

	public static final String MODAL_PLACEHOLDER = "Title, important timestamps, and slots are good to have here.";

	/**
	 * Creates a modal that allows exactly one existing string to be edited in paragraph format.
	 */
	public static Modal editText(ModalEnum modalEnum, String existingText) {
		var id = modalEnum.getId();
		var textInput = buildTextInput(id, modalEnum, existingText);
		return buildModal(id, textInput);
	}

	private static TextInput buildTextInput(String id, ModalEnum modalEnum, String existingText) {
		return TextInput.create(id, modalEnum.getLabel(), TextInputStyle.PARAGRAPH)
			.setMaxLength(Message.MAX_CONTENT_LENGTH)
			.setPlaceholder(modalEnum.getPlaceholder())
			.setValue(existingText)
			.build();
	}

	private static Modal buildModal(String id, ItemComponent input) {
		return Modal.create(id, "VRSVP")
			.addActionRow(input)
			.build();
	}

}
