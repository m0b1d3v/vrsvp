package com.mobiusk.vrsvp.embed;

import com.mobiusk.vrsvp.command.SlashCommandInputs;
import com.mobiusk.vrsvp.util.Parser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class EmbedUi {

	/**
	 * Build a list of embeds (blocks) with an identical number of fields (slots).
	 * <p>
	 * Each embed block has a generic indexed title.
	 * Each field slot has an index and incremented timestamp title, and a starting description.
	 */
	public List<MessageEmbed> build(@Nonnull SlashCommandInputs inputs) {

		var embeds = new LinkedList<MessageEmbed>();
		for (var embedIndex = 0; embedIndex < inputs.getBlocks(); embedIndex++) {
			embeds.add(buildEmbed(inputs, embedIndex));
		}

		return embeds;
	}

	public List<MessageEmbed> editEmbedDescriptionFromAdmin(
		@Nonnull Message message,
		String description,
		int embedIndex
	) {

		var editedEmbeds = new LinkedList<MessageEmbed>();
		var existingEmbeds = message.getEmbeds();

		for (var embedIndexCounter = 0; embedIndexCounter < existingEmbeds.size(); embedIndexCounter++) {

			var embed = existingEmbeds.get(embedIndexCounter);
			var embedBuilder = new EmbedBuilder(embed);

			if (embedIndexCounter == embedIndex) {
				embedBuilder.setDescription(description);
			}

			editedEmbeds.add(embedBuilder.build());
		}

		return editedEmbeds;
	}

	/**
	 * Toggle (add or remove) a user's mention to the specified slot for the given message.
	 */
	public EmbedRsvpToggleResult editEmbedDescriptionFromRSVP(
		@Nonnull Message message,
		@Nonnull String userMention,
		int slotIndexDestination
	) {

		var result = new EmbedRsvpToggleResult();

		var slotIndex = 0;
		for (var embed : message.getEmbeds()) {

			var description = Objects.requireNonNullElse(embed.getDescription(), "");
			var descriptionLines = new LinkedList<>(description.lines().toList());

			for (var lineIndex = 0; lineIndex < descriptionLines.size(); lineIndex++) {

				var line = descriptionLines.get(lineIndex);

				if (Parser.inputIsASlot(line)) {

					if (slotIndex == slotIndexDestination) {
						var editedLine = toggleUserMentionInSlot(line, userMention);
						descriptionLines.set(lineIndex, editedLine);
						result.setUserAddedToSlot(editedLine.length() > line.length());
					}

					slotIndex++;
				}
			}

			result.getMessageEmbeds().add(new EmbedBuilder(embed)
				.setDescription(String.join(Parser.SLOT_DELIMITER, descriptionLines))
				.build()
			);
		}

		return result;
	}

	private MessageEmbed buildEmbed(@Nonnull SlashCommandInputs inputs, int embedIndex) {

		var slotsPerEmbed = inputs.getSlots();
		var slotDurationInSeconds = inputs.getDurationInMinutes() * 60;
		var embedDurationInSeconds = slotsPerEmbed * slotDurationInSeconds;
		var embedStartTimestamp = inputs.getStartTimestamp() + (embedIndex * embedDurationInSeconds);

		var description = new LinkedList<String>();

		description.add(String.format("**Block %d**%n", embedIndex + 1));

		for (var slotIndex = 0; slotIndex < slotsPerEmbed; slotIndex++) {

			var slotNumber = (embedIndex * slotsPerEmbed) + slotIndex + 1;
			var slotTimestamp = embedStartTimestamp + (slotDurationInSeconds * slotIndex);

			var line = String.format("> #%d%s<t:%d:t>", slotNumber, Parser.SIGNUP_DELIMITER, slotTimestamp);
			description.add(line);
		}

		return new EmbedBuilder()
			.setDescription(String.join(Parser.SLOT_DELIMITER, description))
			.build();
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
