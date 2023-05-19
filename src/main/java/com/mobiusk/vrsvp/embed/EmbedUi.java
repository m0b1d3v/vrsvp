package com.mobiusk.vrsvp.embed;

import com.mobiusk.vrsvp.command.SlashCommandInputs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class EmbedUi {

	public static final String SLOT_TEXT_PREFIX = ">>> ";
	public static final String EMPTY_SLOT_TEXT = SLOT_TEXT_PREFIX + "Open";

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

	/**
	 * Toggle (add or remove) a user's mention to the specified slot for the given message.
	 * <p>
	 * This is a particularly ugly function because we cannot edit embed fields in place, they are in unmodifiable lists.
	 * I might be able to clean this up or break it apart later, but for now there's not a clear path.
	 */
	public List<MessageEmbed> toggleRsvp(
		@Nonnull List<MessageEmbed> existingEmbeds,
		@Nonnull String userMention,
		int slotIndexDestination
	) {

		var editedEmbeds = new LinkedList<MessageEmbed>();

		var slotIndex = 0;
		for (var embed : existingEmbeds) {

			var embedBuilder = new EmbedBuilder(embed).clearFields();

			for (var field : embed.getFields()) {

				var name = field.getName();
				var value = field.getValue();

				if (slotIndex == slotIndexDestination) {
					value = editEmbedFieldValueForUserMention(value, userMention);
				}

				embedBuilder.addField(new MessageEmbed.Field(name, value, true));
				slotIndex++;
			}

			editedEmbeds.add(embedBuilder.build());
		}

		return editedEmbeds;
	}

	public List<MessageEmbed> editEmbed(
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

	public List<MessageEmbed> editFieldTitle(
		@Nonnull List<MessageEmbed> existingEmbeds,
		String fieldTitle,
		int fieldIndex
	) {

		var editedEmbeds = new LinkedList<MessageEmbed>();

		var fieldIndexCount = 0;
		for (var embed : existingEmbeds) {

			var embedBuilder = new EmbedBuilder(embed).clearFields();

			for (var field : embed.getFields()) {

				var name = field.getName();
				var value = field.getValue();

				if (fieldIndexCount == fieldIndex) {
					name = fieldTitle;
				}

				embedBuilder.addField(new MessageEmbed.Field(name, value, true));
				fieldIndexCount++;
			}

			editedEmbeds.add(embedBuilder.build());
		}

		return editedEmbeds;
	}

	public List<MessageEmbed> editFieldValue(
		@Nonnull List<MessageEmbed> existingEmbeds,
		String fieldValue,
		int fieldIndex
	) {

		var editedEmbeds = new LinkedList<MessageEmbed>();

		var fieldIndexCount = 0;
		for (var embed : existingEmbeds) {

			var embedBuilder = new EmbedBuilder(embed).clearFields();

			for (var field : embed.getFields()) {

				var name = field.getName();
				var value = field.getValue();

				if (fieldIndexCount == fieldIndex) {
					value = fieldValue;
				}

				embedBuilder.addField(new MessageEmbed.Field(name, value, true));
				fieldIndexCount++;
			}

			editedEmbeds.add(embedBuilder.build());
		}

		return editedEmbeds;
	}

	private MessageEmbed buildEmbed(@Nonnull SlashCommandInputs inputs, int embedIndex) {

		var title = String.format("Block %d", embedIndex + 1);

		var embedBuilder = new EmbedBuilder().setTitle(title);
		for (var fieldIndex = 0; fieldIndex < inputs.getSlots(); fieldIndex++) {
			embedBuilder.addField(buildEmbedField(inputs, embedIndex, fieldIndex));
		}

		return embedBuilder.build();
	}

	private MessageEmbed.Field buildEmbedField(@Nonnull SlashCommandInputs inputs, int embedIndex, int fieldIndex) {

		var slotsPerBlock = inputs.getSlots();
		var slotIndex = (embedIndex * slotsPerBlock) + fieldIndex;
		var slotTimestamp = inputs.getStartTimestamp() + (inputs.getDurationInMinutes() * 60 * slotIndex);

		var fieldName = String.format("#%d - <t:%d:t>", slotIndex + 1, slotTimestamp);
		var fieldValue = addPrefixToFieldValue("");

		return new MessageEmbed.Field(fieldName, fieldValue, true);
	}

	private String editEmbedFieldValueForUserMention(String fieldValue, String userMention) {

		if (fieldValue == null) {
			fieldValue = "";
		}

		fieldValue = fieldValue.replace(EMPTY_SLOT_TEXT, "");
		fieldValue = fieldValue.replace(SLOT_TEXT_PREFIX, "");
		fieldValue = toggleUserMentionInFieldValue(fieldValue, userMention);

		return addPrefixToFieldValue(fieldValue);
	}

	private String toggleUserMentionInFieldValue(String fieldValue, String userMention) {

		var userMentions = new LinkedList<>(Arrays.stream(fieldValue.split("\n"))
			.filter(mention -> !mention.isBlank())
			.toList()
		);

		var userAlreadySignedUp = fieldValue.contains(userMention);
		if (userAlreadySignedUp) {
			userMentions.removeIf(existingMention -> existingMention.equals(userMention));
		} else {
			userMentions.add(userMention);
		}

		return String.join("\n", userMentions);
	}

	private String addPrefixToFieldValue(String fieldValue) {

		if (fieldValue.isBlank()) {
			fieldValue = EMPTY_SLOT_TEXT;
		} else {
			fieldValue = SLOT_TEXT_PREFIX + fieldValue;
		}

		return fieldValue;
	}

}
