package com.mobiusk.vrsvp.embed;

import com.mobiusk.vrsvp.util.Parser;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.util.LinkedList;

@UtilityClass
public class EmbedUi {

	/**
	 * Create a new embed with a description from admin input.
	 */
	public static MessageEmbed createEmbedDescriptionFromAdmin(String description) {
		return new EmbedBuilder()
			.setDescription(description)
			.build();
	}

	/**
	 * Copy an existing message embed and replace the description for admin edits.
	 */
	public static MessageEmbed editEmbedDescriptionFromAdmin(@Nonnull Message message, String description) {

		var embed = message.getEmbeds().get(0);

		return new EmbedBuilder(embed)
			.setDescription(description)
			.build();
	}

	/**
	 * Toggle (add or remove) a user's mention to the specified slot for the given message.
	 */
	public static MessageEmbed editEmbedDescriptionFromRSVP(
		@Nonnull Message message,
		@Nonnull String userMention,
		int slotIndexDestination
	) {

		var slotIndex = 0;

		var description = Parser.readMessageDescription(message);

		var descriptionLines = new LinkedList<>(description.lines().toList());

		for (var lineIndex = 0; lineIndex < descriptionLines.size(); lineIndex++) {

			var line = descriptionLines.get(lineIndex);

			if (Parser.isSlot(line)) {

				if (slotIndex == slotIndexDestination) {
					var editedLine = toggleUserMentionInSlot(line, userMention);
					descriptionLines.set(lineIndex, editedLine);
				}

				slotIndex++;
			}
		}

		var embed = message.getEmbeds().get(0);
		return new EmbedBuilder(embed)
			.setDescription(String.join(Parser.SLOT_DELIMITER, descriptionLines))
			.build();
	}

	private static String toggleUserMentionInSlot(String input, String userMention) {

		var userMentions = Parser.splitSlotText(input);

		var userAlreadySignedUp = userMentions.contains(userMention);
		if (userAlreadySignedUp) {
			userMentions.removeIf(existingMention -> existingMention.equals(userMention));
		} else {
			userMentions.add(userMention);
		}

		return String.join(Parser.SIGNUP_DELIMITER, userMentions);
	}

}
