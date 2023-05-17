package com.mobiusk.vrsvp.autocomplete;

import com.mobiusk.vrsvp.input.InputsEnum;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AutoCompleteReply {

	// While these are all int, when mapped to autocomplete choices later they require longs
	public static final Map<String, List<Long>> OPTIONS = Map.of(
		InputsEnum.BLOCKS.getInput(), List.of(1L, 2L, 3L, 4L),
		InputsEnum.DURATION.getInput(), List.of(60L, 30L, 20L, 15L),
		InputsEnum.SLOTS.getInput(), List.of(1L, 2L, 3L, 4L)
	);

	public void run(
		@Nonnull CommandAutoCompleteInteractionEvent event,
		@Nonnull String inputName
	) {

		var reply = OPTIONS.getOrDefault(inputName, Collections.emptyList());

		if ( ! reply.isEmpty()) {
			event.replyChoiceLongs(reply).queue();
		}
	}

}
