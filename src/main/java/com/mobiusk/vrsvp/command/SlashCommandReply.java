package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.button.ButtonUi;
import com.mobiusk.vrsvp.embed.EmbedUi;
import com.mobiusk.vrsvp.util.Formatter;
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
	public void rsvpCreation(@Nonnull SlashCommandInteractionEvent event, @Nonnull SlashCommandInputs inputs) {

		var validationErrorMessage =SlashCommandValidation.buildValidationErrorMessage(inputs);
		if ( ! validationErrorMessage.isBlank()) {
			event.reply(validationErrorMessage).setEphemeral(true).queue();
			return;
		}

		event.reply(buildEventDescription(inputs))
			.addEmbeds(embedUi.build(inputs))
			.addActionRow(buttonUi.buildRsvpActionPrompts())
			.queue();
	}

	private String buildEventDescription(@Nonnull SlashCommandInputs inputs) {

		var description = Formatter.replies("""
			**New Event**

			Slots start <t:%d:R> on <t:%d:F> and each is %d minute(s) long.%s%s"""
		);

		return String.format(
			description,
			inputs.getStartTimestamp(),
			inputs.getStartTimestamp(),
			inputs.getDurationInMinutes(),
			buildRsvpLimitAddendum(SlashCommandEnum.RSVP_LIMIT_PER_SLOT, inputs.getRsvpLimitPerSlot()),
			buildRsvpLimitAddendum(SlashCommandEnum.RSVP_LIMIT_PER_PERSON, inputs.getRsvpLimitPerPerson())
		);
	}

	private String buildRsvpLimitAddendum(SlashCommandEnum slashCommandEnum, Integer limit) {

		var description = "";

		if (limit != null) {
			description = String.format("%n%s: %d", slashCommandEnum.getDescription(), limit);
		}

		return description;
	}

}
