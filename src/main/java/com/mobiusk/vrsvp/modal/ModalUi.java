package com.mobiusk.vrsvp.modal;

import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class ModalUi {

	/**
	 * Creates a modal that allows exactly one existing string to be edited in paragraph format.
	 */
	public Modal editText(ModalEnum modalEnum, String existingText, Integer contextId) {

		var id = modalEnum.getId();
		if (contextId != null) {
			id = String.format("%s:%d", id, contextId);
		}

		var textInput = buildTextInput(id, modalEnum, existingText);
		return buildModal(id, textInput);
	}

	private TextInput buildTextInput(String id, ModalEnum modalEnum, String existingText) {
		return TextInput.create(id, modalEnum.getLabel(), TextInputStyle.PARAGRAPH)
			.setPlaceholder(modalEnum.getPlaceholder())
			.setValue(existingText)
			.setRequired(false)
			.build();
	}

	private Modal buildModal(String id, ItemComponent input) {
		return Modal.create(id, "VRSVP Edit")
			.addActionRow(input)
			.build();
	}

}
