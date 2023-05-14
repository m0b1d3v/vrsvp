package com.mobiusk.vrsvp;

import com.mobiusk.vrsvp.input.InputsEnum;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
import java.util.Map;

@UtilityClass
public class Commands {

	public static final String SLASH = "vrsvp";

	// While these are all int, when mapped to autocomplete choices later they require longs
	public static final Map<String, List<Long>> INPUT_AUTOCOMPLETE_OPTIONS = Map.of(
		InputsEnum.BLOCKS.getInput(), List.of(1L, 2L, 3L, 4L),
		InputsEnum.DURATION.getInput(), List.of(60L, 30L, 20L, 15L),
		InputsEnum.SLOTS.getInput(), List.of(1L, 2L, 3L, 4L)
	);

	/**
	 * Builds the only slash command this bot concerns itself with and supplies the required input options
	 */
	public static SlashCommandData create() {

		var data = net.dv8tion.jda.api.interactions.commands.build.Commands.slash(SLASH, "Create virtual RSVP")
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
