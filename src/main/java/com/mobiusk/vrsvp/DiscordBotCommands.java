package com.mobiusk.vrsvp;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@UtilityClass
public class DiscordBotCommands {

	public static final String SLASH = "vrsvp";

	/**
	 * Builds the only slash command this bot concerns itself with and supplies the required input options
	 */
	public static SlashCommandData slash() {

		var data = Commands.slash(SLASH, "Create virtual RSVP")
			.setDefaultPermissions(DefaultMemberPermissions.DISABLED);

		addOption(data, DiscordBotCommandInputsEnum.START, "Timestamp from https://hammertime.cyou/ like '1684043839'");
		addOption(data, DiscordBotCommandInputsEnum.BLOCKS, "One each for separate set of slots, like for each DJ at a rave");
		addOption(data, DiscordBotCommandInputsEnum.SLOTS, "How many slots to have for each block");
		addOption(data, DiscordBotCommandInputsEnum.DURATION, "How long each slot will last in minutes");

		return data;
	}

	private static void addOption(SlashCommandData data, DiscordBotCommandInputsEnum input, String description) {
		data.addOption(OptionType.INTEGER, input.getInput(), description, true, false);
	}

}
