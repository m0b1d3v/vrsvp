package com.mobiusk.vrsvp;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Collections;

public class DiscordBotEventListener extends ListenerAdapter {

	/**
	 * Supplies auto-complete options for our named inputs during RSVP creation.
	 */
	@Override
	public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {

		var inputName = event.getFocusedOption().getName();

		var autoCompleteOptions = DiscordBotCommands.INPUT_AUTOCOMPLETE_OPTIONS.getOrDefault(inputName, Collections.emptyList());

		if ( ! autoCompleteOptions.isEmpty()) {
			event.replyChoiceLongs(autoCompleteOptions).queue();
		}
	}

	/**
	 * When slash command is submitted to create an RSVP, the magic will happen here.
	 */
	@Override
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		if ( ! DiscordBotCommands.SLASH.equals(event.getName())) {
			return;
		}

		var blocks = getSlashCommandInput(event, DiscordBotInputsEnum.BLOCKS);
		var slots = getSlashCommandInput(event, DiscordBotInputsEnum.SLOTS);
		var duration = getSlashCommandInput(event, DiscordBotInputsEnum.DURATION);
		var start = getSlashCommandInput(event, DiscordBotInputsEnum.START);

		var reply = buildSlashCommandReply(blocks, slots, duration, start);

		event.reply(reply)
			.setEphemeral(true)
			.queue();
	}

	private String buildSlashCommandReply(int blocks, int slots, int duration, int start) {
		return String.format(
			"Will build RSVP form with %d blocks, %d slots each, %d minutes per slot, starting at <t:%d:F>",
			blocks, slots, duration, start
		);
	}

	private int getSlashCommandInput(@Nonnull SlashCommandInteractionEvent event, DiscordBotInputsEnum inputsEnum) {

		var option = event.getOption(inputsEnum.getInput());
		if (option == null) {
			return 0;
		}

		return option.getAsInt();
	}

}
