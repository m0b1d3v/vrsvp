package com.mobiusk.vrsvp.embed;

import com.mobiusk.vrsvp.command.SlashCommandInputs;
import com.mobiusk.vrsvp.util.Parser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class EmbedUi {

	private static final String SIGNUP_DELIMITER = ", ";
	private static final String SLOT_DELIMITER = "\n";

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

	public List<MessageEmbed> editEmbedTitle(
		@Nonnull List<MessageEmbed> existingEmbeds,
		String embedTitle,
		int embedIndex
	) {

		var editedEmbeds = new LinkedList<MessageEmbed>();

		for (var embedIndexCounter = 0; embedIndexCounter < existingEmbeds.size(); embedIndexCounter++) {

			var embed = existingEmbeds.get(embedIndexCounter);
			var embedBuilder = new EmbedBuilder(embed);

			if (embedIndexCounter == embedIndex) {
				embedBuilder.setTitle(embedTitle);
			}

			editedEmbeds.add(embedBuilder.build());
		}

		return editedEmbeds;
	}

	public List<MessageEmbed> editEmbedDescriptionFromAdmin(
		@Nonnull List<MessageEmbed> existingEmbeds,
		String description,
		int embedIndex
	) {

		var editedEmbeds = new LinkedList<MessageEmbed>();

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
	public List<MessageEmbed> editEmbedDescriptionFromRSVP(
		@Nonnull List<MessageEmbed> existingEmbeds,
		@Nonnull String userMention,
		int slotIndexDestination
	) {

		var editedEmbeds = new LinkedList<MessageEmbed>();

		var slotIndex = 0;
		for (var embed : existingEmbeds) {

			var embedBuilder = new EmbedBuilder(embed);

			var slotsInEmbed = Parser.countSlotsInMessageEmbed(embed);
			if ((slotIndex + slotsInEmbed) >= slotIndexDestination) {

				slotIndexDestination -= slotIndex;

				var embedDescription = Objects.requireNonNullElse(embed.getDescription(), "");
				embedDescription = toggleUserMentionInSlot(embedDescription, userMention, slotIndexDestination);

				embedBuilder.setDescription(embedDescription);
				slotIndexDestination = 999; // Never hit this logic again, but still go through the other embeds
			}

			editedEmbeds.add(embedBuilder.build());
			slotIndex += slotsInEmbed;
		}

		return editedEmbeds;
	}

	private MessageEmbed buildEmbed(@Nonnull SlashCommandInputs inputs, int embedIndex) {
		return new EmbedBuilder()
			.setTitle(buildEmbedTitle(embedIndex))
			.setDescription(buildEmbedDescription(inputs, embedIndex))
			.build();
	}

	private String buildEmbedTitle(int embedIndex) {
		return String.format("Block %d", embedIndex + 1);
	}

	private String buildEmbedDescription(@Nonnull SlashCommandInputs inputs, int embedIndex) {

		var slotsPerEmbed = inputs.getSlots();
		var slotDurationInSeconds = inputs.getDurationInMinutes() * 60;
		var embedDurationInSeconds = slotsPerEmbed * slotDurationInSeconds;
		var embedStartTimestamp = inputs.getStartTimestamp() + (embedIndex * embedDurationInSeconds);

		var description = new LinkedList<String>();
		for (var slotIndex = 0; slotIndex < slotsPerEmbed; slotIndex++) {
			var slotTimestamp = embedStartTimestamp + (slotDurationInSeconds * slotIndex);
			var line = String.format("> #%d%s<t:%d:t>", slotIndex + 1, SIGNUP_DELIMITER, slotTimestamp);
			description.add(line);
		}

		return String.join(SLOT_DELIMITER, description);
	}

	private String toggleUserMentionInSlot(String description, String userMention, int slotIndex) {

		var embedLines = description.split("\n");

		var slotsFoundIndexCounter = 0;
		for (var embedLinesCounter = 0; embedLinesCounter < embedLines.length; embedLinesCounter++) {
			if (Parser.inputIsASlot(embedLines[embedLinesCounter])) {
				if (slotIndex == slotsFoundIndexCounter) {
					slotIndex = embedLinesCounter;
					break;
				}
				slotsFoundIndexCounter++;
			}
		}

		var userMentions = new LinkedList<>(Arrays.stream(embedLines[slotIndex].split(SIGNUP_DELIMITER))
			.filter(mention -> !mention.isBlank())
			.toList()
		);

		var userAlreadySignedUp = userMentions.contains(userMention);
		if (userAlreadySignedUp) {
			userMentions.removeIf(existingMention -> existingMention.equals(userMention));
		} else {
			userMentions.add(userMention);
		}

		embedLines[slotIndex] = String.join(SIGNUP_DELIMITER, userMentions);

		return String.join(SLOT_DELIMITER, embedLines);
	}

}
