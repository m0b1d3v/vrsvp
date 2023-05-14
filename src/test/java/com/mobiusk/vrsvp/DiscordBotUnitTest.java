package com.mobiusk.vrsvp;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiscordBotUnitTest extends TestBase {

	@InjectMocks
	private DiscordBot discordBot;

	@Spy
	private JDABuilder jdaBuilder = DiscordBot.create("Testing");

	@Mock
	private JDA jda;

	@Mock
	private CommandListUpdateAction commandListUpdateAction;

	@Captor
	private ArgumentCaptor<CommandData> commandDataArgumentCaptor;

	@BeforeEach
	public void beforeEach() {

		doReturn(jda).when(jdaBuilder).build();

		when(jda.updateCommands()).thenReturn(commandListUpdateAction);

		when(commandListUpdateAction.addCommands(any(CommandData.class))).thenReturn(commandListUpdateAction);
	}

	@Test
	void startingDiscordBotWithoutTokenThrowsException() {

		// In direct contradiction to what we set up in beforeEach for other tests, check this edge case
		doCallRealMethod().when(jdaBuilder).build();

		jdaBuilder.setToken(null);
		var ex = assertThrows(IllegalArgumentException.class, () -> discordBot.start());
		assertEquals("Token may not be null", ex.getMessage());
	}

	@Test
	void startingDiscordBotWaitsForItToBeReady() throws InterruptedException {

		startDiscordBot();

		verify(jdaBuilder).build();
		verify(jda).awaitReady();
	}

	@Test
	void eventListenersAreAdded() {

		startDiscordBot();

		verify(jda).addEventListener(any(DiscordBotEventListener.class));
	}

	@Test
	void botSlashCommandsAreAdded() {

		startDiscordBot();

		verify(jda).updateCommands();
		verify(commandListUpdateAction).addCommands(any(CommandData.class));
		verify(commandListUpdateAction).queue();
	}

	@Test
	void botSlashCommandIsWellFormedAndOnlyUsableByAdmins() {

		startDiscordBot();

		verify(commandListUpdateAction).addCommands(commandDataArgumentCaptor.capture());

		var command = commandDataArgumentCaptor.getValue();

		assertEquals(Command.Type.SLASH, command.getType());
		assertEquals(DiscordBot.SLASH_COMMAND, command.getName());
		assertEquals(DefaultMemberPermissions.DISABLED, command.getDefaultPermissions());
	}

	// Test utility method(s)

	private void startDiscordBot() {
		try {
			discordBot.start();
		} catch (InterruptedException e) {
			fail("Discord bot should have started without interruption for this test case", e);
		}
	}

}
