package com.mobiusk.vrsvp.button;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonEnum {

	EDIT("edit", "Edit"),
	EDIT_DESCRIPTION("edit-description", "Description"),
	EDIT_EMBED("edit-embed", "Block"),
	EDIT_FIELD_TITLE("edit-field-title", "Slot Titles"),
	EDIT_FIELD_VALUE("edit-field-value", "Slot Values"),
	RSVP("rsvp", "RSVP"),
	SIGNUP("signup", "Signup"),
	UNKNOWN("unknown", "UNKNOWN");

	private final String id;
	private final String label;

	public static ButtonEnum getById(String id) {

		for (ButtonEnum buttonEnum : ButtonEnum.values()) {
			if (buttonEnum.getId().equals(id)) {
				return buttonEnum;
			}
		}

		return ButtonEnum.UNKNOWN;
	}

}
