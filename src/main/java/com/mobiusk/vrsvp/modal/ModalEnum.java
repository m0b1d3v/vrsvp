package com.mobiusk.vrsvp.modal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModalEnum {

	EVENT_CREATION("event-creation", "Event Creation", ModalUi.MODAL_PLACEHOLDER),
	EVENT_DESCRIPTION("event-description", "Event Description", ModalUi.MODAL_PLACEHOLDER),
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
