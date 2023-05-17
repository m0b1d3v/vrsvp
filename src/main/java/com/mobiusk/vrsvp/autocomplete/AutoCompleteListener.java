package com.mobiusk.vrsvp.autocomplete;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class AutoCompleteListener extends ListenerAdapter {

	// Class constructor field(s)
	private final AutoCompleteReply autoCompleteReply;

	/**
	 * Supplies auto-complete options for our named inputs during RSVP creation.
	 */
	@Override
	public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {
		var inputName = event.getFocusedOption().getName();
		autoCompleteReply.run(event, inputName);
	}

}
