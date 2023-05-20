package com.mobiusk.vrsvp.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.util.Objects;

@UtilityClass
public class Parser {

	public static final String SIGNUP_DELIMITER = ", ";

	public static final String SLOT_DELIMITER = "\n";
	public static int countSlotsInMessageEmbeds(@Nonnull Message message) {
		return message.getEmbeds().stream().mapToInt(Parser::countSlotsInMessageEmbed).sum();
	}

	public static int countSlotsInMessageEmbed(@Nonnull MessageEmbed messageEmbed) {

		var embedDescription = Objects.requireNonNullElse(messageEmbed.getDescription(), "");

		return (int) embedDescription
			.lines()
			.filter(Parser::inputIsASlot)
			.count();
	}

	public static boolean inputIsASlot(String input) {
		return input.startsWith("> #");
	}

	public static List<String> readDataInSlot(String input) {
		return new LinkedList<>(Arrays.stream(input.split(SIGNUP_DELIMITER)).toList());
	}

}
