package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.input.Inputs;
import com.mobiusk.vrsvp.input.InputsValidation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class OutputsCommand {

	public static final String EMPTY_SLOT_TEXT = ">>> Open";

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

		var embeds = new LinkedList<MessageEmbed>();
		for (var embedIndex = 0; embedIndex < inputs.getBlocks(); embedIndex++) {
			embeds.add(buildEmbed(inputs, embedIndex));
		}

		event.reply(eventDescription)
			.addEmbeds(embeds)
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

	private MessageEmbed buildEmbed(@Nonnull Inputs inputs, int embedIndex) {

		var title = String.format("Block %d", embedIndex + 1);

		var embedBuilder = new EmbedBuilder().setTitle(title);
		for (var fieldIndex = 0; fieldIndex < inputs.getSlots(); fieldIndex++) {
			embedBuilder.addField(buildEmbedField(inputs, embedIndex, fieldIndex));
		}

		return embedBuilder.build();
	}

	private MessageEmbed.Field buildEmbedField(@Nonnull Inputs inputs, int embedIndex, int fieldIndex) {

		var slotsPerBlock = inputs.getSlots();
		var slotIndex = (embedIndex * slotsPerBlock) + fieldIndex;
		var slotTimestamp = inputs.getStartTimestamp() + (inputs.getDurationInMinutes() * 60 * slotIndex);

		var fieldName = String.format("#%d - <t:%d:t>", slotIndex + 1, slotTimestamp);

		return new MessageEmbed.Field(fieldName, EMPTY_SLOT_TEXT, true);
	}

}
