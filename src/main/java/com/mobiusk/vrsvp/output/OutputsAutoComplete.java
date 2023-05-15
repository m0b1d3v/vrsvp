package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.Commands;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

import javax.annotation.Nonnull;
import java.util.Collections;

public class OutputsAutoComplete {

	public void reply(
		@Nonnull CommandAutoCompleteInteractionEvent event,
		@Nonnull String inputName
	) {

		var reply = Commands.INPUT_AUTOCOMPLETE_OPTIONS.getOrDefault(inputName, Collections.emptyList());

		if ( ! reply.isEmpty()) {
			event.replyChoiceLongs(reply).queue();
		}
	}

}
