package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.input.InputsEnum;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlashCommandUiUnitTest extends TestBase {

	private final SlashCommandData command = SlashCommandUi.create();

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(SlashCommandUi.class);
	}

	@Test
	void formed() {
		assertEquals(Command.Type.SLASH, command.getType());
		assertEquals(SlashCommandUi.INVOCATION, command.getName());
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
	void inputsUseLogicalOrder() {
		var options = command.getOptions();
		assertEquals(4, options.size());
		assertEquals(InputsEnum.START.getInput(), options.get(0).getName());
		assertEquals(InputsEnum.BLOCKS.getInput(), options.get(1).getName());
		assertEquals(InputsEnum.SLOTS.getInput(), options.get(2).getName());
		assertEquals(InputsEnum.DURATION.getInput(), options.get(3).getName());
	}

}
