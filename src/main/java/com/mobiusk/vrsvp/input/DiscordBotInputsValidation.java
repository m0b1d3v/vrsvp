package com.mobiusk.vrsvp.input;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.util.List;

@UtilityClass
public class DiscordBotInputsValidation {

	// 9 (The 10th is used for event information)
	public static final int BLOCKS_MAXIMUM = Message.MAX_EMBED_COUNT - 1;

	public static final int BLOCKS_AND_SLOTS_PRODUCT_MAXIMUM = SelectMenu.OPTIONS_MAX_AMOUNT;

	// One week
	public static final int DURATION_IN_MINUTES_MAXIMUM = 60 * 24 * 7;

	// 2035-01-01
	public static final int TIMESTAMP_MAXIMUM = 2_051_244_000;

    public static String buildValidationErrorMessage(DiscordBotInputs inputs) {

        var validationMessages = List.of(

            validateBlocksMaximum(inputs.getBlocks()),
			validateBlocksMinimum(inputs.getBlocks()),

			validateDurationMaximum(inputs.getDurationInMinutes()),
			validateDurationMinimum(inputs.getDurationInMinutes()),

            validateSlotsMaximum(inputs.getBlocks(), inputs.getSlots()),
			validateSlotsMinimum(inputs.getSlots()),

			validateStartTimestampMaximum(inputs.getStartTimestamp()),
			validateStartTimestampMinimum(inputs.getStartTimestamp())
        );

		var validationErrors = validationMessages.stream().filter(message -> ! message.isBlank()).toList();

		return String.join("\n", validationErrors);
    }

    private static String validateBlocksMaximum(int blocks) {

        if (blocks <= BLOCKS_MAXIMUM) {
            return "";
        }

        var message = "The maximum amount of blocks allowed in VRSVP is %d due to a Discord limitation, as 1 is reserved for event information."
			+ " Please retry this command with a smaller block count, or split your RSVP into more than one form.";

        return String.format(message, BLOCKS_MAXIMUM, BLOCKS_MAXIMUM);
    }

	private static String validateBlocksMinimum(int blocks) {

		var min = 1;

		if (blocks >= min) {
			return "";
		}

		return "The minimum amount of blocks required in VRSVP is 1, otherwise there is nothing to RSVP for."
			+ " Please retry this command with a larger block count.";
	}

	private static String validateDurationMaximum(int durationInMinutes) {

		if (durationInMinutes <= DURATION_IN_MINUTES_MAXIMUM) {
			return "";
		}

		return "The maximum duration in minutes for each slot in VRSVP is equal to one week."
			+ " This is a mostly-arbitrary decision on our part to guard from any programming errors."
			+ " If you have a legitimate need for this edge case, we are willing to adjust it."
			+ " Please retry this command with a smaller duration.";
	}

	private static String validateDurationMinimum(int durationInMinutes) {

		var min = 1;

		if (durationInMinutes >= min) {
			return "";
		}

		return "The minimum duration in minutes for each slot in VRSVP is one minute."
			+ " Please retry this command with a larger duration.";
	}

    private static String validateSlotsMaximum(int blocks, int slots) {

		if ((blocks * slots) <= BLOCKS_AND_SLOTS_PRODUCT_MAXIMUM) {
			return "";
		}

		var message = "The maximum amount of (blocks * slots) allowed in VRSVP is %d due to a Discord limitation."
			+ " Please retry this command with a smaller total block/slot count, or split your RSVP into more than one form.";

		return String.format(message, BLOCKS_AND_SLOTS_PRODUCT_MAXIMUM, BLOCKS_AND_SLOTS_PRODUCT_MAXIMUM);
    }

	private static String validateSlotsMinimum(int slots) {

		var min = 1;

		if (slots >= min) {
			return "";
		}

		return "The minimum amount of slots required in VRSVP is 1, otherwise there is nothing to RSVP for."
			+ " Please retry this command with a larger slots count.";
	}

	private static String validateStartTimestampMaximum(int startTimestamp) {

		if (startTimestamp <= TIMESTAMP_MAXIMUM) {
			return "";
		}

		return "The maximum start timestamp in VRSVP equates to 2035-01-01."
			+ " This is a mostly-arbitrary decision on our part to guard from any programming errors."
			+ " If you have a legitimate need for this edge case, we are willing to adjust it."
			+ " Please retry this command with a smaller start timestamp.";
	}

	private static String validateStartTimestampMinimum(int startTimestamp) {

		var min = 0;

		if (startTimestamp >= min) {
			return "";
		}

		return "The minimum start timestamp in VRSVP is 0, which equates to 1970-01-01."
			+ " This is to ensure compatibility with Discord timestamp formatting."
			+ " Please retry this command with a larger start timestamp.";
	}

}
