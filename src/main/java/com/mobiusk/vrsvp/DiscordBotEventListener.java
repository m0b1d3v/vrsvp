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
		// Empty
	}

}
