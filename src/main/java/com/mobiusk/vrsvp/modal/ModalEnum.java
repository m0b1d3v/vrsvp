package com.mobiusk.vrsvp.modal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModalEnum {

	DESCRIPTION("description", "Description", "Event title and important timestamps from https://hammertime.cyou/ are good to have here."),
	EMBED("block", "Block", "What makes this block unique?"),
	FIELD_TITLE("slot-title", "Slot Title", "Number and timestamp for this slot."),
	FIELD_VALUE("slot-value", "Slot Value", "Signups for this slot"),
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
