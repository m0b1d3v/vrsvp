package com.mobiusk.vrsvp;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
