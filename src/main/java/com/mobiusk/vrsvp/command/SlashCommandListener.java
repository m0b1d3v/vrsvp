package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.util.Formatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
@Slf4j
public class SlashCommandListener extends ListenerAdapter {

	// Class constructor field(s)
	private final SlashCommandReply reply;

	/**
	 * When slash command is submitted to create an RSVP, the magic will happen here.
	 */
	@Override
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		if ( ! SlashCommandUi.INVOCATION.equals(event.getName())) {

			log.warn(
				Formatter.logMarkers(event).and(Formatter.logMarker("eventName", event.getName())),
				"Unrecognized slash command received"
			);

			return;
		}

		handleSlashCommandInteraction(event);
	}

	private void handleSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		var inputs = new SlashCommandInputs();
		inputs.setBlocks(getSlashCommandInput(event, SlashCommandEnum.BLOCKS));
		inputs.setDurationInMinutes(getSlashCommandInput(event, SlashCommandEnum.DURATION));
		inputs.setSlots(getSlashCommandInput(event, SlashCommandEnum.SLOTS));
		inputs.setStartTimestamp(getSlashCommandInput(event, SlashCommandEnum.START));
		inputs.setRsvpLimitPerSlot(getSlashCommandInput(event, SlashCommandEnum.RSVP_LIMIT_PER_SLOT));
		inputs.setRsvpLimitPerPerson(getSlashCommandInput(event, SlashCommandEnum.RSVP_LIMIT_PER_PERSON));

		log.info(
			Formatter.logMarkers(event).and(Formatter.logMarker("inputs", inputs)),
			"Slash command received"
		);

		reply.rsvpCreation(event, inputs);
	}

	private Integer getSlashCommandInput(@Nonnull SlashCommandInteractionEvent event, SlashCommandEnum slashCommandEnum) {

		var option = event.getOption(slashCommandEnum.getId());
		if (option == null) {
			return null;
		}

		return option.getAsInt();
	}

}
