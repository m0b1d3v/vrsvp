package com.mobiusk.vrsvp.embed;

import com.mobiusk.vrsvp.command.SlashCommandEnum;
import com.mobiusk.vrsvp.command.SlashCommandInputs;
import com.mobiusk.vrsvp.util.Parser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.Objects;

public class EmbedUi {

	/**
	 * Build an RSVP form everyone can see with a given number of time slot, each with an index and incremented timestamp title.
	 */
	public MessageEmbed build(@Nonnull SlashCommandInputs inputs) {

		var slotDurationInSeconds = inputs.getDurationInMinutes() * 60;

		var description = new LinkedList<String>();
		description.add("**New Event**\n");
		description.add(String.format("- Starts <t:%d:R> on <t:%d:F>", inputs.getStartTimestamp(), inputs.getStartTimestamp()));
		description.add(String.format("- Each slot is %d minutes long", inputs.getDurationInMinutes()));
		description.add(buildRsvpLimitAddendum(SlashCommandEnum.RSVP_LIMIT_PER_SLOT, inputs.getRsvpLimitPerSlot()));
		description.add(buildRsvpLimitAddendum(SlashCommandEnum.RSVP_LIMIT_PER_PERSON, inputs.getRsvpLimitPerPerson()));
		description.add("");

		for (var slotIndex = 0; slotIndex < inputs.getSlots(); slotIndex++) {
			var slotTimestamp = inputs.getStartTimestamp() + (slotDurationInSeconds * slotIndex);
			var line = String.format("> #%d%s<t:%d:t>", slotIndex + 1, Parser.SIGNUP_DELIMITER, slotTimestamp);
			description.add(line);
		}

		description.removeIf(Objects::isNull);

		return new EmbedBuilder()
			.setDescription(String.join(Parser.SLOT_DELIMITER, description))
			.build();
	}

	public MessageEmbed editEmbedDescriptionFromAdmin(@Nonnull Message message, String description) {

		var embed = message.getEmbeds().get(0);

		return new EmbedBuilder(embed)
			.setDescription(description)
			.build();
	}

	/**
	 * Toggle (add or remove) a user's mention to the specified slot for the given message.
	 */
	public MessageEmbed editEmbedDescriptionFromRSVP(
		@Nonnull Message message,
		@Nonnull String userMention,
		int slotIndexDestination
	) {

		var slotIndex = 0;

		var embed = message.getEmbeds().get(0);
		var description = Objects.requireNonNullElse(embed.getDescription(), "");

		var descriptionLines = new LinkedList<>(description.lines().toList());

		for (var lineIndex = 0; lineIndex < descriptionLines.size(); lineIndex++) {

			var line = descriptionLines.get(lineIndex);

			if (Parser.inputIsASlot(line)) {

				if (slotIndex == slotIndexDestination) {
					var editedLine = toggleUserMentionInSlot(line, userMention);
					descriptionLines.set(lineIndex, editedLine);
				}

				slotIndex++;
			}
		}

		return new EmbedBuilder(embed)
			.setDescription(String.join(Parser.SLOT_DELIMITER, descriptionLines))
			.build();
	}

	private String buildRsvpLimitAddendum(SlashCommandEnum slashCommandEnum, Integer limit) {

		if (limit != null) {
			return String.format("- %n%s: %d", slashCommandEnum.getDescription(), limit);
		}

		return null;
	}

	private String toggleUserMentionInSlot(String input, String userMention) {

		var userMentions = Parser.readDataInSlot(input);

		var userAlreadySignedUp = userMentions.contains(userMention);
		if (userAlreadySignedUp) {
			userMentions.removeIf(existingMention -> existingMention.equals(userMention));
		} else {
			userMentions.add(userMention);
		}

		return String.join(Parser.SIGNUP_DELIMITER, userMentions);
	}

}
