package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
	void inputsAreMarkedRequiredAsNeeded() {
		var options = command.getOptions();
		assertTrue(options.get(0).isRequired());
		assertTrue(options.get(1).isRequired());
		assertTrue(options.get(2).isRequired());
		assertTrue(options.get(3).isRequired());
		assertFalse(options.get(4).isRequired());
		assertFalse(options.get(5).isRequired());
	}

	@Test
	void inputsAreNumbers() {
		assertTrue(command.getOptions().stream().allMatch(option -> option.getType() == OptionType.INTEGER));
	}

	@Test
	void inputsUseLogicalOrder() {
		var options = command.getOptions();
		assertEquals(6, options.size());
		assertEquals(SlashCommandEnum.START.getId(), options.get(0).getName());
		assertEquals(SlashCommandEnum.BLOCKS.getId(), options.get(1).getName());
		assertEquals(SlashCommandEnum.SLOTS.getId(), options.get(2).getName());
		assertEquals(SlashCommandEnum.DURATION.getId(), options.get(3).getName());
		assertEquals(SlashCommandEnum.RSVP_LIMIT_PER_PERSON.getId(), options.get(4).getName());
		assertEquals(SlashCommandEnum.RSVP_LIMIT_PER_SLOT.getId(), options.get(5).getName());
	}

}
