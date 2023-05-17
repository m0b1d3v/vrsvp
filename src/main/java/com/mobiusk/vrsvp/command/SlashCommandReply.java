package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.input.Inputs;
import com.mobiusk.vrsvp.input.InputsValidation;
import com.mobiusk.vrsvp.button.ButtonUi;
import com.mobiusk.vrsvp.embed.EmbedUi;
import com.mobiusk.vrsvp.output.MessageFormatter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class SlashCommandReply {

	// Class constructor field(s)
	private final ButtonUi buttonUi;
	private final EmbedUi embedUi;

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
			.addEmbeds(embedUi.build(inputs))
			.addActionRow(buttonUi.buildRsvpActionPrompts())
			.queue();
	}

	private String buildEventDescription(@Nonnull Inputs inputs) {

		var description = MessageFormatter.output("""
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

}
