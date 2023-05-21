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

	public static String readMessageDescription(@Nonnull Message message) {
		var embed = message.getEmbeds().get(0);
		return Objects.requireNonNullElse(embed.getDescription(), "");
	}

	public static int countSlotsInText(String text) {
		return (int) text.lines()
			.filter(Parser::inputIsASlot)
			.count();
	}

	public static boolean inputIsASlot(String input) {
		return input.startsWith("> #");
	}

	public static List<String> readDataInSlot(String input) {
		return new LinkedList<>(Arrays.stream(input.split(SIGNUP_DELIMITER)).toList());
	}

	public static Integer findRsvpLimitPerPersonInText(String text) {
		return runRegexPatternToFindNumberInText(RSVP_LIMIT_PER_PERSON_PATTERN, text);
	}

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
