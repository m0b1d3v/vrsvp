package com.mobiusk.vrsvp;

import com.mobiusk.vrsvp.input.Inputs;
import com.mobiusk.vrsvp.input.InputsEnum;
import com.mobiusk.vrsvp.output.OutputsAutoComplete;
import com.mobiusk.vrsvp.output.OutputsButtons;
import com.mobiusk.vrsvp.output.OutputsReplies;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class EventListener extends ListenerAdapter {

	// Class constructor field(s)
	private final OutputsAutoComplete outputsAutoComplete;
	private final OutputsReplies outputsReplies;

	/**
	 * Direct all button presses from bot messages based on the button ID.
	 * <p>
	 * Button IDs sometimes include extra context by splitting information with ":", like "signup:1".
	 */
	@Override
	public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
		switch (getButtonInteractionAction(event)) {
			case OutputsButtons.RSVP -> handleRsvpButtonPress(event);
			case OutputsButtons.SIGNUP -> handleSignupButtonPress(event);
			default -> outputsReplies.ephemeralReply(event, "Input not recognized.");
		}
	}

	/**
	 * Supplies auto-complete options for our named inputs during RSVP creation.
	 */
	@Override
	public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {
		var inputName = event.getFocusedOption().getName();
		outputsAutoComplete.reply(event, inputName);
	}

	/**
	 * When slash command is submitted to create an RSVP, the magic will happen here.
	 */
	@Override
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		if ( ! Commands.SLASH.equals(event.getName())) {
			return;
		}

		handleSlashCommandInteraction(event);
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

	private String getButtonInteractionContext(@Nonnull ButtonInteractionEvent event) {

		var buttonId = getButtonInteractionId(event);
		if (buttonId.length < 2) {
			return null;
		}

		return buttonId[1];
	}

	private void handleRsvpButtonPress(@Nonnull ButtonInteractionEvent event) {

		var slots = 0;
		for (var embed : event.getMessage().getEmbeds()) {
			slots += embed.getFields().size();
		}

		outputsReplies.rsvpInterest(event, slots);
	}

	private void handleSignupButtonPress(@Nonnull ButtonInteractionEvent event) {

		var buttonIndex = getButtonInteractionContext(event);
		if (buttonIndex == null) {
			return;
		}

		var rsvp = getEphemeralButtonEventSource(event);
		if (rsvp == null) {
			return;
		}

		var userMention = event.getUser().getAsMention();
		var slotIndex = Integer.parseInt(buttonIndex);
		outputsReplies.rsvpToggle(event, rsvp, userMention, slotIndex);
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

	// Private slash command logic

	private int getSlashCommandInput(@Nonnull SlashCommandInteractionEvent event, InputsEnum inputsEnum) {

		var option = event.getOption(inputsEnum.getInput());
		if (option == null) {
			return -1;
		}

		return option.getAsInt();
	}

	private void handleSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		var inputs = new Inputs();
		inputs.setBlocks(getSlashCommandInput(event, InputsEnum.BLOCKS));
		inputs.setSlots(getSlashCommandInput(event, InputsEnum.SLOTS));
		inputs.setDurationInMinutes(getSlashCommandInput(event, InputsEnum.DURATION));
		inputs.setStartTimestamp(getSlashCommandInput(event, InputsEnum.START));

		outputsReplies.rsvpCreation(event, inputs);
	}

}
