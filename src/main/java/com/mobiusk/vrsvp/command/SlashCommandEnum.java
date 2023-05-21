package com.mobiusk.vrsvp.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

@Getter
@RequiredArgsConstructor
public enum SlashCommandEnum {

	DURATION(
		"duration",
		60 * 24 * 7, // One week in seconds
		1,
		"How long each slot will last in minutes",
		true
	),

	RSVP_LIMIT_PER_SLOT(
		"rsvp-limit-per-slot",
		null,
		0,
		"Maximum number of people that can RSVP for a single slot",
		false
	),

	RSVP_LIMIT_PER_PERSON(
		"rsvp-limit-per-person",
		null,
		0,
		"Maximum number of slots a person can RSVP for",
		false
	),

	SLOTS(
		"slots",
		SelectMenu.OPTIONS_MAX_AMOUNT, // 25
		1,
		"Number of time slots available for signing up",
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
