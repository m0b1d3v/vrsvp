package com.mobiusk.vrsvp;

import com.mobiusk.vrsvp.input.Inputs;
import com.mobiusk.vrsvp.input.InputsEnum;
import com.mobiusk.vrsvp.output.OutputsAutoComplete;
import com.mobiusk.vrsvp.output.OutputsCommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class EventListener extends ListenerAdapter {

	// Class constructor field(s)
	private final OutputsAutoComplete outputsAutoComplete;
	private final OutputsCommand outputsCommand;

	/**
	 * Supplies auto-complete options for our named inputs during RSVP creation.
	 */
	@Override
	public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {
		var inputName = event.getFocusedOption().getName();
		outputsAutoComplete.reply(event, inputName);
	}

	/**
	 * When slash command is submitted to create an RSVP, the magic will happen here.
	 */
	@Override
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		if ( ! Commands.SLASH.equals(event.getName())) {
			return;
		}

		var inputs = new Inputs();
		inputs.setBlocks(getSlashCommandInput(event, InputsEnum.BLOCKS));
		inputs.setSlots(getSlashCommandInput(event, InputsEnum.SLOTS));
		inputs.setDurationInMinutes(getSlashCommandInput(event, InputsEnum.DURATION));
		inputs.setStartTimestamp(getSlashCommandInput(event, InputsEnum.START));

		outputsCommand.reply(event, inputs);
	}

	private int getSlashCommandInput(@Nonnull SlashCommandInteractionEvent event, InputsEnum inputsEnum) {

		var option = event.getOption(inputsEnum.getInput());
		if (option == null) {
			return -1;
		}

		return option.getAsInt();
	}

}
