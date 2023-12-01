package dev.m0b1.vrsvp;

import dev.m0b1.vrsvp.command.SlashCommandReply;
import dev.m0b1.vrsvp.command.SlashCommandUi;
import dev.m0b1.vrsvp.button.ButtonListener;
import dev.m0b1.vrsvp.command.SlashCommandListener;
import dev.m0b1.vrsvp.button.ButtonReply;
import dev.m0b1.vrsvp.logging.LogData;
import dev.m0b1.vrsvp.logging.ServiceLog;
import dev.m0b1.vrsvp.modal.ModalListener;
import dev.m0b1.vrsvp.modal.ModalReply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.event.Level;

import java.util.EnumSet;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class Bot {

	private final JDABuilder jdaBuilder;
	private final ServiceLog serviceLog;

	private JDA jda;

	/**
	 * Create a Java Discord API gateway with no intents desired and using low memory by not caching any information.
	 */
	public static JDABuilder create(String discordBotToken) {
		return JDABuilder.createLight(discordBotToken, EnumSet.noneOf(GatewayIntent.class));
	}

	/**
	 * Connect with Discord servers and start processing any events or commands that are received.
	 *
	 * @throws InterruptedException If Discord servers could not be connected with.
	 */
	public void start() throws InterruptedException {

		jda = discordBotLogin();

		waitForDiscordConnection();
		addEventListeners();
		logKnownGuilds();
		updateBotSlashCommands();
	}

	private JDA discordBotLogin() {
		return jdaBuilder.build();
	}

	private void waitForDiscordConnection() throws InterruptedException {
		jda.awaitReady();
	}

	/**
	 * We love dependency injection!
	 */
	private void addEventListeners() {

		var buttonReply = new ButtonReply(serviceLog);
		var slashCommandReply = new SlashCommandReply();
		var modalReply = new ModalReply();

		jda.addEventListener(new ButtonListener(buttonReply, serviceLog));
		jda.addEventListener(new SlashCommandListener(serviceLog, slashCommandReply));
		jda.addEventListener(new ModalListener(modalReply, serviceLog));
	}

	private void logKnownGuilds() {

		var guildNames = jda.getGuilds()
			.stream()
			.map(Guild::getName)
			.toList();

		serviceLog.run(LogData.builder()
			.level(Level.INFO)
			.message("Known guilds")
			.markers(Map.of("guilds", guildNames))
		);
	}

	private void updateBotSlashCommands() {

		var slashCommand = SlashCommandUi.create();

		jda.updateCommands()
			.addCommands(slashCommand)
			.queue();
	}

}
