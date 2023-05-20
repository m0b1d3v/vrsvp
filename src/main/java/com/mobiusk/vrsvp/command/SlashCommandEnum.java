package com.mobiusk.vrsvp.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

@Getter
@RequiredArgsConstructor
public enum SlashCommandEnum {

	BLOCKS(
		"blocks",
		Message.MAX_EMBED_COUNT, // 10
		1,
		"One each for separate set of slots, like for each DJ at a rave",
		true
	),

	DURATION(
		"duration",
		60 * 24 * 7, // One week in seconds
		1,
		"How long each slot will last in minutes",
		true
	),

	SLOTS(
		"slots",
		SelectMenu.OPTIONS_MAX_AMOUNT, // 25 (extra validation on blocks * slots occurs)
		1,
		"How many slots to have for each block",
		true
	),

	START(
		"start",
		2_051_244_000, // 2035-01-01
		0,
		"Timestamp from https://hammertime.cyou/ like '1684043839'",
		true
	);

	private final String id;
	private final Integer maximum;
	private final Integer minimum;
	private final String description;
	private final boolean required;

}
