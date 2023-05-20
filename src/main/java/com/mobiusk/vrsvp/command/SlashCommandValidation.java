package com.mobiusk.vrsvp.command;

import lombok.experimental.UtilityClass;

import java.util.Collections;

@UtilityClass
public class SlashCommandValidation {

	public static String buildValidationErrorMessage(SlashCommandInputs inputs) {

		var validationMessages = Collections.singletonList(
			validateSlotsInBlocksMaximum(inputs.getBlocks(), inputs.getSlots())
		);

		var validationErrors = validationMessages.stream().filter(message -> ! message.isBlank()).toList();

		return String.join("\n", validationErrors);
	}

	private static String validateSlotsInBlocksMaximum(int blocks, int slots) {

		var maximum = SlashCommandEnum.SLOTS.getMaximum();

		if ((blocks * slots) <= maximum) {
			return "";
		}

		var message = "The maximum amount of (blocks * slots) allowed in VRSVP is %d due to a Discord limitation."
			+ " Please retry this command with a smaller total block/slot count, or split your RSVP into more than one form.";

		return String.format(message, maximum, maximum);
	}

}
