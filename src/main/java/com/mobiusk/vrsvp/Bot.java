package com.mobiusk.vrsvp;

import com.mobiusk.vrsvp.command.SlashCommandReply;
import com.mobiusk.vrsvp.command.SlashCommandUi;
import com.mobiusk.vrsvp.button.ButtonListener;
import com.mobiusk.vrsvp.command.SlashCommandListener;
import com.mobiusk.vrsvp.button.ButtonReply;
import com.mobiusk.vrsvp.modal.ModalListener;
import com.mobiusk.vrsvp.modal.ModalReply;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.EnumSet;

@RequiredArgsConstructor
public class Bot {

	private final JDABuilder jdaBuilder;

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

		var buttonReply = new ButtonReply();
		var slashCommandReply = new SlashCommandReply();
		var modalReply = new ModalReply();

		jda.addEventListener(new ButtonListener(buttonReply));
		jda.addEventListener(new SlashCommandListener(slashCommandReply));
		jda.addEventListener(new ModalListener(modalReply));
	}

	private void updateBotSlashCommands() {

		var slashCommand = SlashCommandUi.create();

		jda.updateCommands()
			.addCommands(slashCommand)
			.queue();
	}

}
