package com.mobiusk.vrsvp.command;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class SlashCommandListener extends ListenerAdapter {

	// Class constructor field(s)
	private final SlashCommandReply reply;

	/**
	 * When slash command is submitted to create an RSVP, the magic will happen here.
	 */
	@Override
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		if ( ! SlashCommandUi.INVOCATION.equals(event.getName())) {
			return;
		}

		handleSlashCommandInteraction(event);
	}

	private int getSlashCommandInput(@Nonnull SlashCommandInteractionEvent event, SlashCommandEnum slashCommandEnum) {

		var option = event.getOption(slashCommandEnum.getId());
		if (option == null) {
			return -1;
		}

		return option.getAsInt();
	}

	private void handleSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		var inputs = new SlashCommandInputs();
		inputs.setBlocks(getSlashCommandInput(event, SlashCommandEnum.BLOCKS));
		inputs.setSlots(getSlashCommandInput(event, SlashCommandEnum.SLOTS));
		inputs.setDurationInMinutes(getSlashCommandInput(event, SlashCommandEnum.DURATION));
		inputs.setStartTimestamp(getSlashCommandInput(event, SlashCommandEnum.START));

		reply.rsvpCreation(event, inputs);
	}

}
