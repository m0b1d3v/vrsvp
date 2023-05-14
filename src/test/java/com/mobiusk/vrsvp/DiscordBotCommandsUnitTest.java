package com.mobiusk.vrsvp;

import com.mobiusk.vrsvp.input.DiscordBotInputsEnum;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscordBotCommandsUnitTest extends TestBase {

	private final SlashCommandData command = DiscordBotCommands.create();

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(DiscordBotCommands.class);
	}

	@Test
	void formed() {
		assertEquals(Command.Type.SLASH, command.getType());
		assertEquals(DiscordBotCommands.SLASH, command.getName());
	}

	@Test
	void onlyUsableByAdmins() {
		assertEquals(DefaultMemberPermissions.DISABLED, command.getDefaultPermissions());
	}

	@Test
	void inputsAreRequired() {
		assertTrue(command.getOptions().stream().allMatch(OptionData::isRequired));
	}

	@Test
	void inputsAreNumbers() {
		assertTrue(command.getOptions().stream().allMatch(option -> option.getType() == OptionType.INTEGER));
	}

	@Test
	void inputsUseLogicalOrderAndAutocompleteAppropriately() {
		var options = command.getOptions();
		assertEquals(4, options.size());
		checkOptionNameAndAutocomplete(options.get(0), DiscordBotInputsEnum.START, false);
		checkOptionNameAndAutocomplete(options.get(1), DiscordBotInputsEnum.BLOCKS, true);
		checkOptionNameAndAutocomplete(options.get(2), DiscordBotInputsEnum.SLOTS, true);
		checkOptionNameAndAutocomplete(options.get(3), DiscordBotInputsEnum.DURATION, true);
	}

	// Test utility method(s)

	private void checkOptionNameAndAutocomplete(OptionData option, DiscordBotInputsEnum inputsEnum, boolean autoComplete) {
		assertEquals(inputsEnum.getInput(), option.getName());
		assertEquals(autoComplete, option.isAutoComplete());
	}

}
