package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.TestBase;
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
		assertEquals(SlashCommandEnum.START.getId(), options.get(0).getName());
		assertEquals(SlashCommandEnum.BLOCKS.getId(), options.get(1).getName());
		assertEquals(SlashCommandEnum.SLOTS.getId(), options.get(2).getName());
		assertEquals(SlashCommandEnum.DURATION.getId(), options.get(3).getName());
	}

}
