package com.mobiusk.vrsvp.input;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InputsEnum {

	BLOCKS("blocks"),
	DURATION("duration"),
	SLOTS("slots"),
	START("start");

	@Getter
	private final String input;

}
