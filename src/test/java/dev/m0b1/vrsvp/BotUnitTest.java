package dev.m0b1.vrsvp;

import dev.m0b1.vrsvp.button.ButtonListener;
import dev.m0b1.vrsvp.command.SlashCommandListener;
import dev.m0b1.vrsvp.logging.ServiceLog;
import dev.m0b1.vrsvp.modal.ModalListener;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BotUnitTest extends TestBase {

	@InjectMocks private Bot bot;

	@Mock private ButtonListener buttonListener;
	@Mock private ModalListener modalListener;
	@Mock private ServiceLog serviceLog;
	@Mock private SlashCommandListener slashCommandListener;

	@Mock private CommandListUpdateAction commandListUpdateAction;

	@BeforeEach
	public void beforeEach() {

		when(jda.updateCommands()).thenReturn(commandListUpdateAction);
		when(jda.getGuilds()).thenReturn(Collections.emptyList());

		when(commandListUpdateAction.addCommands(any(CommandData.class))).thenReturn(commandListUpdateAction);
	}

	@Test
	void startingDiscordBotWaitsForItToBeReady() throws InterruptedException {

		startDiscordBot();

		verify(jda).awaitReady();
	}

	@Test
	void eventListenersAreAdded() {

		startDiscordBot();

		verify(jda).addEventListener(any(ButtonListener.class));
		verify(jda).addEventListener(any(SlashCommandListener.class));
		verify(jda).addEventListener(any(ModalListener.class));
	}

	@Test
	void knownGuildsAreLogged() {

		startDiscordBot();

		verify(jda).getGuilds();
	}

	@Test
	void botSlashCommandsAreAdded() {

		startDiscordBot();

		verify(jda).updateCommands();
		verify(commandListUpdateAction).addCommands(any(CommandData.class));
		verify(commandListUpdateAction).queue();
	}

	// Test utility method(s)

	private void startDiscordBot() {
		try {
			bot.postConstruct();
		} catch (InterruptedException e) {
			fail("Discord bot should have started without interruption for this test case", e);
		}
	}

}
