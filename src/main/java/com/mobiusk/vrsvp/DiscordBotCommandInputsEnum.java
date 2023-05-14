package com.mobiusk.vrsvp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DiscordBotCommandInputsEnum {

	START("start"),
	BLOCKS("blocks"),
	SLOTS("slots"),
	DURATION("duration");

	@Getter
	private final String input;

}
