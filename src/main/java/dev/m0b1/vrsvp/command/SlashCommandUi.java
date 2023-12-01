package dev.m0b1.vrsvp.command;

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
	 * Builds the only slash command this bot concerns itself with and supplies the required input options.
	 */
	public static SlashCommandData create() {

		var data = Commands.slash(INVOCATION, "Create virtual RSVP")
			.setDefaultPermissions(DefaultMemberPermissions.DISABLED);

		// Order matters here, this was chosen for the most logical flow
		addOption(data, SlashCommandEnum.START);
		addOption(data, SlashCommandEnum.SLOTS);
		addOption(data, SlashCommandEnum.DURATION);
		addOption(data, SlashCommandEnum.RSVP_LIMIT_PER_PERSON);
		addOption(data, SlashCommandEnum.RSVP_LIMIT_PER_SLOT);

		return data;
	}

	private static void addOption(
		SlashCommandData data,
		SlashCommandEnum input
	) {

		var optionData = new OptionData(OptionType.INTEGER, input.getId(), input.getDescription())
			.setRequired(input.isRequired())
			.setMinValue(input.getMinimum());

		if (input.getMaximum() != null) {
			optionData.setMaxValue(input.getMaximum());
		}

		data.addOptions(optionData);
	}

}
