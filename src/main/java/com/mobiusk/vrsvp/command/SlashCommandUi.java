package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.input.InputsEnum;
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
		addOption(data, InputsEnum.START);
		addOption(data, InputsEnum.BLOCKS);
		addOption(data, InputsEnum.SLOTS);
		addOption(data, InputsEnum.DURATION);

		return data;
	}

	private static void addOption(
		SlashCommandData data,
		InputsEnum input
	) {

		var optionData = new OptionData(OptionType.INTEGER, input.getId(), input.getDescription());
		optionData.setMaxValue(input.getMaximum());
		optionData.setMinValue(input.getMinimum());
		optionData.setRequired(true);
		optionData.setAutoComplete(false);

		data.addOptions(optionData);
	}

}
