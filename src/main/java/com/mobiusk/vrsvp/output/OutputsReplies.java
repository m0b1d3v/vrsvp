package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.input.Inputs;
import com.mobiusk.vrsvp.input.InputsValidation;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class OutputsReplies {

	public static final String REPLY_PREFIX = "---\n";
	public static final String REPLY_SUFFIX = "\n---";

	// Class constructor field(s)
	private final OutputsButtons outputsButtons;
	private final OutputsEmbeds outputsEmbeds;

	/**
	 * Replies with an ephemeral validation error or an RSVP form everyone can use.
	 */
	public void rsvpCreation(@Nonnull SlashCommandInteractionEvent event, @Nonnull Inputs inputs) {

		var validationErrorMessage = InputsValidation.buildValidationErrorMessage(inputs);
		if ( ! validationErrorMessage.isBlank()) {
			event.reply(validationErrorMessage).setEphemeral(true).queue();
			return;
		}

		buildEventSignup(event, inputs);
	}

	private void buildEventSignup(@Nonnull SlashCommandInteractionEvent event, @Nonnull Inputs inputs) {
		event.reply(buildEventDescription(inputs))
			.addEmbeds(outputsEmbeds.build(inputs))
			.addActionRow(outputsButtons.buildRsvpActionPrompts())
			.queue();
	}

	private String buildEventDescription(@Nonnull Inputs inputs) {

		var description = formattedMessage("""
			**Signups are now available for a new event**

			Slots start <t:%d:R> on <t:%d:F> and each is %d minute(s) long."""
		);

		return String.format(
			description,
			inputs.getStartTimestamp(),
			inputs.getStartTimestamp(),
			inputs.getDurationInMinutes()
		);
	}

	private String formattedMessage(String message) {
		return REPLY_PREFIX + message + REPLY_SUFFIX;
	}

}
