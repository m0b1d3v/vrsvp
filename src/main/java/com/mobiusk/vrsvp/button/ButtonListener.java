package com.mobiusk.vrsvp.button;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class ButtonListener extends ListenerAdapter {

	// Class constructor field(s)
	private final ButtonReply reply;

	/**
	 * Direct all button presses from bot messages based on the button ID.
	 * <p>
	 * Button IDs sometimes include extra context by splitting information with ":", like "signup:1".
	 */
	@Override
	public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
		switch (getButtonInteractionAction(event)) {
			case ButtonUi.RSVP -> handleRsvpButtonPress(event);
			case ButtonUi.SIGNUP -> handleSignupButtonPress(event);
			default -> reply.ephemeral(event, "Input not recognized.");
		}
	}

	// Private button interaction logic

	private String[] getButtonInteractionId(@Nonnull ButtonInteractionEvent event) {
		var buttonId = event.getComponentId();
		return buttonId.split(":");
	}

	private String getButtonInteractionAction(@Nonnull ButtonInteractionEvent event) {
		var buttonId = getButtonInteractionId(event);
		return buttonId[0];
	}

	private Integer getButtonInteractionContext(@Nonnull ButtonInteractionEvent event) {

		var buttonId = getButtonInteractionId(event);
		if (buttonId.length < 2) {
			return null;
		}

		var contextIndex = buttonId[1];
		return Integer.parseInt(contextIndex);
	}

	private void handleRsvpButtonPress(@Nonnull ButtonInteractionEvent event) {

		var slots = 0;
		for (var embed : event.getMessage().getEmbeds()) {
			slots += embed.getFields().size();
		}

		reply.rsvp(event, slots);
	}

	private void handleSignupButtonPress(@Nonnull ButtonInteractionEvent event) {

		var slotIndex = getButtonInteractionContext(event);
		if (slotIndex == null) {
			return;
		}

		var rsvp = getEphemeralButtonEventSource(event);
		if (rsvp == null) {
			return;
		}

		var userMention = event.getUser().getAsMention();
		reply.signup(event, rsvp, userMention, slotIndex);
	}

	private Message getEphemeralButtonEventSource(ButtonInteractionEvent event) {

		var eventMessageReference = event.getMessage().getMessageReference();
		if (eventMessageReference == null) {
			return null;
		}

		return event
			.getChannel()
			.retrieveMessageById(eventMessageReference.getMessageIdLong())
			.complete();
	}

}
