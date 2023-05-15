package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.input.Inputs;
import com.mobiusk.vrsvp.input.InputsValidation;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.annotation.Nonnull;

public class OutputsCommand {

	public void reply(@Nonnull SlashCommandInteractionEvent event, @Nonnull Inputs inputs) {

		var reply = buildReply(inputs);

		event.reply(reply)
			.setEphemeral(true)
			.queue();
	}

	private String buildReply(@Nonnull Inputs inputs) {

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

}
