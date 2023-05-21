package com.mobiusk.vrsvp.util;

import com.mobiusk.vrsvp.command.SlashCommandEnum;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@UtilityClass
public class Parser {

	public static final String SIGNUP_DELIMITER = ", ";

	public static final String SLOT_DELIMITER = "\n";

	private static final Pattern RSVP_LIMIT_PER_PERSON_PATTERN = buildRegexPatternForLimit(SlashCommandEnum.RSVP_LIMIT_PER_PERSON);

	private static final Pattern RSVP_LIMIT_PER_SLOT_PATTERN = buildRegexPatternForLimit(SlashCommandEnum.RSVP_LIMIT_PER_SLOT);

	/**
	 * Read the message description from the first embed, defaulting to an empty string if not found.
	 */
	public static String readMessageDescription(@Nonnull Message message) {
		var embed = message.getEmbeds().get(0);
		return Objects.requireNonNullElse(embed.getDescription(), "");
	}

	/**
	 * Determines if given line starts with the necessary characters to indicate a slot.
	 */
	public static boolean isSlot(@Nonnull String line) {
		return line.startsWith("> #");
	}

	/**
	 * @see Parser#isSlot for more information.
	 */
	public static int countSlotsInText(@Nonnull String text) {
		return (int) text.lines()
			.filter(Parser::isSlot)
			.count();
	}

	/**
	 * Given some slot text, separate them based on known delimiter and convert to a modifiable list.
	 */
	public static List<String> splitSlotText(@Nonnull String text) {
		return new LinkedList<>(Arrays.stream(text.split(SIGNUP_DELIMITER)).toList());
	}

	/**
	 * Find the first result matching the RSVP limit per person pattern and return the desired integer if found.
	 */
	public static Integer findRsvpLimitPerPersonInText(String text) {
		return runRegexPatternToFindNumberInText(RSVP_LIMIT_PER_PERSON_PATTERN, text);
	}

	/**
	 * Find the first result matching the RSVP limit per slot pattern and return the desired integer if found.
	 */
	public static Integer findRsvpLimitPerSlotInText(String text) {
		return runRegexPatternToFindNumberInText(RSVP_LIMIT_PER_SLOT_PATTERN, text);
	}

	private static Pattern buildRegexPatternForLimit(SlashCommandEnum slashCommandEnum) {
		return Pattern.compile(slashCommandEnum.getDescription() + ": (\\d+)");
	}

	private static Integer runRegexPatternToFindNumberInText(Pattern pattern, String text) {

		if (text == null) {
			return null;
		}

		var matches = pattern.matcher(text);
		if ( ! matches.find()) {
			return null;
		}

		var result = matches.group(1);
		return Integer.parseInt(result);
	}

}
