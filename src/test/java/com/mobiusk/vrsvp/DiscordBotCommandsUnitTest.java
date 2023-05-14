package com.mobiusk.vrsvp;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscordBotCommandsUnitTest extends TestBase {

	@Test
	void slashCommandIsWellFormedAndOnlyUsableByAdmins() {

		var command = DiscordBotCommands.slash();

		assertEquals(Command.Type.SLASH, command.getType());
		assertEquals(DiscordBotCommands.SLASH, command.getName());
		assertEquals(DefaultMemberPermissions.DISABLED, command.getDefaultPermissions());
	}

}
