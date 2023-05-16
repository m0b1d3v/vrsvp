package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.input.Inputs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class OutputsEmbeds {

	public static final String SLOT_TEXT_PREFIX = ">>> ";
	public static final String EMPTY_SLOT_TEXT = SLOT_TEXT_PREFIX + "Open";

	/**
	 * Build a list of embeds (blocks) with an identical number of fields (slots).
	 * <p>
	 * Each embed block has a generic indexed title.
	 * Each field slot has an index and incremented timestamp title, and a starting description.
	 */
	public List<MessageEmbed> build(@Nonnull Inputs inputs) {

		var embeds = new LinkedList<MessageEmbed>();
		for (var embedIndex = 0; embedIndex < inputs.getBlocks(); embedIndex++) {
			embeds.add(buildEmbed(inputs, embedIndex));
		}

		return embeds;
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
