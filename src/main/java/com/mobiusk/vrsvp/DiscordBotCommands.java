package com.mobiusk.vrsvp;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
import java.util.Map;

@UtilityClass
public class DiscordBotCommands {

	public static final String SLASH = "vrsvp";

	public static final Map<String, List<Long>> INPUT_AUTOCOMPLETE_OPTIONS = Map.of(
		DiscordBotCommandInputsEnum.BLOCKS.getInput(), List.of(1L, 2L, 3L, 4L),
		DiscordBotCommandInputsEnum.SLOTS.getInput(), List.of(1L, 2L, 3L, 4L),
		DiscordBotCommandInputsEnum.DURATION.getInput(), List.of(60L, 30L, 20L, 15L)
	);

	/**
	 * Builds the only slash command this bot concerns itself with and supplies the required input options
	 */
	public static SlashCommandData slash() {

		var data = Commands.slash(SLASH, "Create virtual RSVP")
			.setDefaultPermissions(DefaultMemberPermissions.DISABLED);

		addOption(data, DiscordBotCommandInputsEnum.START, "Timestamp from https://hammertime.cyou/ like '1684043839'", false);
		addOption(data, DiscordBotCommandInputsEnum.BLOCKS, "One each for separate set of slots, like for each DJ at a rave", true);
		addOption(data, DiscordBotCommandInputsEnum.SLOTS, "How many slots to have for each block", true);
		addOption(data, DiscordBotCommandInputsEnum.DURATION, "How long each slot will last in minutes", true);

		return data;
	}

	private static void addOption(SlashCommandData data, DiscordBotCommandInputsEnum input, String description, boolean autoComplete) {
		data.addOption(OptionType.INTEGER, input.getInput(), description, true, autoComplete);
	}

}
