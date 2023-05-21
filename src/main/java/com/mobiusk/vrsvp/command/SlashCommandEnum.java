package com.mobiusk.vrsvp.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SlashCommandEnum {

	DURATION(
		"duration",
		60L * 24 * 7, // One week in seconds
		1L,
		"How long each slot will last in minutes",
		true
	),

	RSVP_LIMIT_PER_SLOT(
		"rsvp-limit-per-slot",
		null,
		0L,
		"Maximum number of people that can RSVP for a single slot",
		false
	),

	RSVP_LIMIT_PER_PERSON(
		"rsvp-limit-per-person",
		null,
		0L,
		"Maximum number of slots a person can RSVP for",
		false
	),

	SLOTS(
		"slots",
		25L,
		1L,
		"Number of time slots available for signing up",
		true
	),

	START(
		"start",
		2_051_244_000L, // 2035-01-01
		0L,
		"Timestamp from https://hammertime.cyou/ like '1684043839'",
		true
	);

	private final String id;
	private final Long maximum;
	private final Long minimum;
	private final String description;
	private final boolean required;

}
