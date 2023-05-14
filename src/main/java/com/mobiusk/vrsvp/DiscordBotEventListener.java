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

		var inputs = new DiscordBotInputs();
		inputs.setBlocks(getSlashCommandInput(event, DiscordBotInputsEnum.BLOCKS));
		inputs.setSlots(getSlashCommandInput(event, DiscordBotInputsEnum.SLOTS));
		inputs.setDurationInMinutes(getSlashCommandInput(event, DiscordBotInputsEnum.DURATION));
		inputs.setStartTimestamp(getSlashCommandInput(event, DiscordBotInputsEnum.START));

		var reply = buildSlashCommandReply(inputs);

		event.reply(reply)
			.setEphemeral(true)
			.queue();
	}

	private String buildSlashCommandReply(DiscordBotInputs inputs) {
		return String.format(
			"Will build RSVP form with %d blocks, %d slots each, %d minutes per slot, starting at <t:%d:F>",
			inputs.getBlocks(),
			inputs.getSlots(),
			inputs.getDurationInMinutes(),
			inputs.getStartTimestamp()
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
