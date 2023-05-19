package com.mobiusk.vrsvp.modal;

import com.mobiusk.vrsvp.util.Formatter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class ModalUi {

	public static final String DESCRIPTION = "description";
	public static final String EMBED = "block";
	public static final String FIELD_TITLE = "slot-title";
	public static final String FIELD_VALUE = "slot-value";

	/**
	 * Creates a modal that allows exactly one existing string to be edited in paragraph format.
	 */
	public Modal editText(String id, String placeholder, String existingText) {
		var textInput = buildTextInput(id, placeholder, existingText);
		return buildModal(id, textInput);
	}

	private TextInput buildTextInput(String id, String placeholder, String existingText) {

		var label = Formatter.idAsLabel(id);

		return TextInput.create(id, label, TextInputStyle.PARAGRAPH)
			.setPlaceholder(placeholder)
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
