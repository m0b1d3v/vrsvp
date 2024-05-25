package dev.m0b1.vrsvp;

import dev.m0b1.vrsvp.command.SlashCommandUi;
import dev.m0b1.vrsvp.button.ButtonListener;
import dev.m0b1.vrsvp.command.SlashCommandListener;
import dev.m0b1.vrsvp.modal.ModalListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class Bot {

	private final ButtonListener buttonListener;
	private final JDA jda;
	private final ModalListener modalListener;
	private final SlashCommandListener slashCommandListener;

	/**
	 * Connect with Discord servers and start processing any events or commands that are received.
	 *
	 * @throws InterruptedException If Discord servers could not be connected with.
	 */
	@PostConstruct
	public void postConstruct() throws InterruptedException {
		waitForDiscordConnection();
		addEventListeners();
		logKnownGuilds();
		updateBotSlashCommands();
	}

	private void waitForDiscordConnection() throws InterruptedException {
		jda.awaitReady();
	}

	private void addEventListeners() {
		jda.addEventListener(buttonListener);
		jda.addEventListener(slashCommandListener);
		jda.addEventListener(modalListener);
	}

	private void logKnownGuilds() {

		var guildNames = jda.getGuilds()
			.stream()
			.map(Guild::getName)
			.toList();

		log.atInfo()
			.setMessage("Known guilds")
			.addKeyValue("guilds", guildNames)
			.log();
	}

	private void updateBotSlashCommands() {

		var slashCommand = SlashCommandUi.create();

		jda.updateCommands()
			.addCommands(slashCommand)
			.queue();
	}

}
