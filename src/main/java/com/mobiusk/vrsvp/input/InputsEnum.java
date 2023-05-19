package com.mobiusk.vrsvp.input;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

@Getter
@RequiredArgsConstructor
public enum InputsEnum {

	// 10
	BLOCKS("blocks", Message.MAX_EMBED_COUNT, 1, "One each for separate set of slots, like for each DJ at a rave"),

	// One week in seconds
	DURATION("duration", 60*24*7, 1, "How long each slot will last in minutes"),

	// 25
	SLOTS("slots", SelectMenu.OPTIONS_MAX_AMOUNT, 1, "How many slots to have for each block"),

	// 2035-01-01
	START("start", 2_051_244_000, 0, "Timestamp from https://hammertime.cyou/ like '1684043839'");

	private final String id;
	private final int maximum;
	private final int minimum;
	private final String description;

}
