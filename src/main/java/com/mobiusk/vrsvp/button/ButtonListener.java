package com.mobiusk.vrsvp.button;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
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

		var buttonInteractionAction = getButtonInteractionAction(event);

		var buttonEnum = ButtonEnum.getById(buttonInteractionAction);
		switch (buttonEnum) {
			case EDIT -> handleEditButtonPress(event);
			case EDIT_DESCRIPTION -> handleEditDescriptionButtonPress(event);
			case EDIT_EMBED -> handleEditEmbedButtonPress(event);
			case EDIT_FIELD_TITLE -> handleEditFieldTitleButtonPress(event);
			case EDIT_FIELD_VALUE -> handleEditFieldValueButtonPress(event);
			case RSVP -> handleRsvpButtonPress(event);
			case SIGNUP -> handleSignupButtonPress(event);
			default -> reply.ephemeral(event, "Input not recognized.");
		}
	}

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

	private void handleEditButtonPress(@Nonnull ButtonInteractionEvent event) {

		if (accessDenied(event)) {
			return;
		}

		reply.edit(event);
	}

	private void handleEditDescriptionButtonPress(@Nonnull ButtonInteractionEvent event) {

		if (accessDenied(event)) {
			return;
		}

		var rsvp = getEphemeralButtonEventSource(event);
		if (rsvp == null) {
			return;
		}

		reply.editDescription(event, rsvp);
	}

	private void handleEditEmbedButtonPress(@Nonnull ButtonInteractionEvent event) {

		if (accessDenied(event)) {
			return;
		}

		var rsvp = getEphemeralButtonEventSource(event);
		if (rsvp == null) {
			return;
		}

		var embedIndex = getButtonInteractionContext(event);
		if (embedIndex != null) {
			reply.editEmbed(event, rsvp, embedIndex);
		} else {
			var selectionCount = rsvp.getEmbeds().size();
			reply.editIndexedSelectionGeneration(event, "block", ButtonEnum.EDIT_EMBED.getId(), selectionCount);
		}
	}

	private void handleEditFieldTitleButtonPress(@Nonnull ButtonInteractionEvent event) {

		if (accessDenied(event)) {
			return;
		}

		var rsvp = getEphemeralButtonEventSource(event);
		if (rsvp == null) {
			return;
		}

		var fieldIndex = getButtonInteractionContext(event);
		if (fieldIndex != null) {
			reply.editSlotTitle(event, rsvp, fieldIndex);
		} else {
			var selectionCount = countSlotsInMessageEmbeds(rsvp);
			reply.editIndexedSelectionGeneration(event, "slot title", ButtonEnum.EDIT_FIELD_TITLE.getId(), selectionCount);
		}
	}

	private void handleEditFieldValueButtonPress(@Nonnull ButtonInteractionEvent event) {

		if (accessDenied(event)) {
			return;
		}

		var rsvp = getEphemeralButtonEventSource(event);
		if (rsvp == null) {
			return;
		}

		var fieldIndex = getButtonInteractionContext(event);
		if (fieldIndex != null) {
			reply.editSlotValue(event, rsvp, fieldIndex);
		} else {
			var selectionCount = countSlotsInMessageEmbeds(rsvp);
			reply.editIndexedSelectionGeneration(event, "slot value", ButtonEnum.EDIT_FIELD_VALUE.getId(), selectionCount);
		}
	}

	private void handleRsvpButtonPress(@Nonnull ButtonInteractionEvent event) {
		var slots = countSlotsInMessageEmbeds(event.getMessage());
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

	// Any changes here should be propagated to the similar method in ModalListener (no common parent = copied code)
	private Message getEphemeralButtonEventSource(ButtonInteractionEvent event) {

		var eventMessageReference = event.getMessage().getMessageReference();
		if (eventMessageReference == null) {
			return null;
		}

		return event
			.getMessageChannel()
			.retrieveMessageById(eventMessageReference.getMessageIdLong())
			.complete();
	}

	private int countSlotsInMessageEmbeds(@Nonnull Message message) {

		var slots = 0;
		for (var embed : message.getEmbeds()) {
			slots += embed.getFields().size();
		}

		return slots;
	}

	private boolean accessDenied(@Nonnull ButtonInteractionEvent event) {

		var member = event.getMember();

		var accessDenied = member != null && ! member.hasPermission(Permission.ADMINISTRATOR);
		if (accessDenied) {
			reply.ephemeral(event, "Access denied.");
		}

		return accessDenied;
	}

}
