package com.mobiusk.vrsvp.embed;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class EmbedRsvpToggleResult {

	private List<MessageEmbed> messageEmbeds = new LinkedList<>();
	private boolean userAddedToSlot;

}
