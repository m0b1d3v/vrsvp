package com.mobiusk.vrsvp.button;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonEnum {

	EDIT("edit", "Edit"),
	EDIT_DESCRIPTION("edit-description", "Description"),
	EDIT_EMBED_TITLE("edit-embed-title", "Block"),
	EDIT_EMBED_DESCRIPTION("edit-embed-description", "Slots"),
	RSVP("rsvp", "RSVP"),
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
