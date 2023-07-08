package com.mobiusk.vrsvp.button;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonEnum {

	ABOUT("https://mobiusk.com/projects/vrsvp", "About"),
	EDIT("edit", "Edit Event"),
	EDIT_EVENT_ACTIVE("edit-toggle-rsvp-active", "Toggle RSVP Active"),
	EDIT_EVENT_DESCRIPTION("edit-event-description", "Edit Description"),
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
