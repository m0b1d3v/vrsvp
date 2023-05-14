package com.mobiusk.vrsvp;

import com.mobiusk.vrsvp.input.Inputs;
import com.mobiusk.vrsvp.input.InputsEnum;
import com.mobiusk.vrsvp.input.InputsValidation;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Collections;

public class EventListener extends ListenerAdapter {

	/**
	 * Supplies auto-complete options for our named inputs during RSVP creation.
	 */
	@Override
	public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {

		var inputName = event.getFocusedOption().getName();

		var autoCompleteOptions = Commands.INPUT_AUTOCOMPLETE_OPTIONS.getOrDefault(inputName, Collections.emptyList());

		if ( ! autoCompleteOptions.isEmpty()) {
			event.replyChoiceLongs(autoCompleteOptions).queue();
		}
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

		var reply = buildSlashCommandReply(inputs);

		event.reply(reply)
			.setEphemeral(true)
			.queue();
	}

	private String buildSlashCommandReply(Inputs inputs) {

		var validationErrorMessage = InputsValidation.buildValidationErrorMessage(inputs);
		if ( ! validationErrorMessage.isBlank()) {
			return validationErrorMessage;
		}

		return String.format(
			"Will build RSVP form with %d blocks, %d slots each, %d minutes per slot, starting at <t:%d:F>",
			inputs.getBlocks(),
			inputs.getSlots(),
			inputs.getDurationInMinutes(),
			inputs.getStartTimestamp()
		);
	}

	private int getSlashCommandInput(@Nonnull SlashCommandInteractionEvent event, InputsEnum inputsEnum) {

		var option = event.getOption(inputsEnum.getInput());
		if (option == null) {
			return -1;
		}

		return option.getAsInt();
	}

}
