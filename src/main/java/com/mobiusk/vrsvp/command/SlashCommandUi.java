package com.mobiusk.vrsvp.command;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
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
		addOption(data, SlashCommandEnum.START);
		addOption(data, SlashCommandEnum.BLOCKS);
		addOption(data, SlashCommandEnum.SLOTS);
		addOption(data, SlashCommandEnum.DURATION);

		return data;
	}

	private static void addOption(
		SlashCommandData data,
		SlashCommandEnum input
	) {

		var optionData = new OptionData(OptionType.INTEGER, input.getId(), input.getDescription());
		optionData.setAutoComplete(false);

		if (input.isRequired()) {
			optionData.setRequired(true);
		}

		if (input.getMaximum() != null) {
			optionData.setMaxValue(input.getMaximum());
		}

		if (input.getMinimum() != null) {
			optionData.setMinValue(input.getMinimum());
		}

		data.addOptions(optionData);
	}

}
