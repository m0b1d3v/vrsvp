package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.input.Inputs;
import com.mobiusk.vrsvp.input.InputsValidation;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.annotation.Nonnull;

public class OutputsCommand {

	public void reply(@Nonnull SlashCommandInteractionEvent event, @Nonnull Inputs inputs) {

		var validationErrorMessage = InputsValidation.buildValidationErrorMessage(inputs);

		if (validationErrorMessage.isBlank()) {
			buildEventSignup(event, inputs);
		} else {
			event.reply(validationErrorMessage)
				.setEphemeral(true)
				.queue();
		}
	}

	private void buildEventSignup(@Nonnull SlashCommandInteractionEvent event, @Nonnull Inputs inputs) {

		var eventDescription = buildEventDescription(inputs);

		event.reply(eventDescription)
			.queue();
	}

	private String buildEventDescription(@Nonnull Inputs inputs) {

		var description = """
			---
			**Signups are now available for a new event**

			Slots start <t:%d:R> on <t:%d:F> and each is %d minute(s) long.
			---""";

		return String.format(
			description,
			inputs.getStartTimestamp(),
			inputs.getStartTimestamp(),
			inputs.getDurationInMinutes()
		);
	}

}
