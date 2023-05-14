package com.mobiusk.vrsvp;

import com.mobiusk.vrsvp.input.InputsEnum;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandsUnitTest extends TestBase {

	private final SlashCommandData command = Commands.create();

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(Commands.class);
	}

	@Test
	void formed() {
		assertEquals(Command.Type.SLASH, command.getType());
		assertEquals(Commands.SLASH, command.getName());
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
		checkOptionNameAndAutocomplete(options.get(0), InputsEnum.START, false);
		checkOptionNameAndAutocomplete(options.get(1), InputsEnum.BLOCKS, true);
		checkOptionNameAndAutocomplete(options.get(2), InputsEnum.SLOTS, true);
		checkOptionNameAndAutocomplete(options.get(3), InputsEnum.DURATION, true);
	}

	// Test utility method(s)

	private void checkOptionNameAndAutocomplete(OptionData option, InputsEnum inputsEnum, boolean autoComplete) {
		assertEquals(inputsEnum.getInput(), option.getName());
		assertEquals(autoComplete, option.isAutoComplete());
	}

}
