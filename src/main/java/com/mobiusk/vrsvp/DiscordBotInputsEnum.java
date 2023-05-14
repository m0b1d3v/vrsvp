package com.mobiusk.vrsvp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DiscordBotInputsEnum {

	BLOCKS("blocks"),
	DURATION("duration"),
	SLOTS("slots"),
	START("start");

	@Getter
	private final String input;

}
