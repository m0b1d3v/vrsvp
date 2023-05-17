package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.input.InputsEnum;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@UtilityClass
public class SlashCommandUi {

	public static final String INVOCATION = "vrsvp";

	/**
	 * Builds the only slash command this bot concerns itself with and supplies the required input options
	 */
	public static SlashCommandData create() {

		var data = Commands.slash(INVOCATION, "Create virtual RSVP")
			.setDefaultPermissions(DefaultMemberPermissions.DISABLED);

		// Order matters here, this was chosen for the most logical flow
		addOption(data, InputsEnum.START, "Timestamp from https://hammertime.cyou/ like '1684043839'", false);
		addOption(data, InputsEnum.BLOCKS, "One each for separate set of slots, like for each DJ at a rave", true);
		addOption(data, InputsEnum.SLOTS, "How many slots to have for each block", true);
		addOption(data, InputsEnum.DURATION, "How long each slot will last in minutes", true);

		return data;
	}

	private static void addOption(SlashCommandData data, InputsEnum input, String description, boolean autoComplete) {
		data.addOption(OptionType.INTEGER, input.getInput(), description, true, autoComplete);
	}

}
