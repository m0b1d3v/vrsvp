package com.mobiusk.vrsvp.modal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModalEnum {

	EVENT_DESCRIPTION("event-description", "Event", "Event title and important timestamps from https://hammertime.cyou/ are good to have here."),
	EMBED_DESCRIPTION("embed-description", "Block", "Signups for this block."),
	UNKNOWN("unknown", "Unknown", "Unknown");

	private final String id;
	private final String label;
	private final String placeholder;

	public static ModalEnum getById(String id) {

		for (ModalEnum modalEnum : ModalEnum.values()) {
			if (modalEnum.getId().equals(id)) {
				return modalEnum;
			}
		}

		return ModalEnum.UNKNOWN;
	}

}
