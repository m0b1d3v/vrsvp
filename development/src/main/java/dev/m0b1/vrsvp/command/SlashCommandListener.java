package dev.m0b1.vrsvp.command;

import dev.m0b1.vrsvp.util.Mdc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;

@RequiredArgsConstructor
@Service
@Slf4j
public class SlashCommandListener extends ListenerAdapter {

	// Class constructor field(s)
	private final SlashCommandReply reply;

	/**
	 * When slash command is submitted to create an RSVP, the magic will happen here.
	 */
	@Override
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		Mdc.put(event);

		if ( ! SlashCommandUi.INVOCATION.equals(event.getName())) {

			log.atWarn()
				.setMessage("Unrecognized slash command received")
				.addKeyValue("eventName", event.getName())
				.log();

			return;
		}

		handleSlashCommandInteraction(event);
	}

	private void handleSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		Mdc.put(event);

		var inputs = new SlashCommandInputs();
		inputs.setDurationInMinutes(getSlashCommandInput(event, SlashCommandEnum.DURATION));
		inputs.setSlots(getSlashCommandInput(event, SlashCommandEnum.SLOTS));
		inputs.setStartTimestamp(getSlashCommandInput(event, SlashCommandEnum.START));
		inputs.setRsvpLimitPerSlot(getSlashCommandInput(event, SlashCommandEnum.RSVP_LIMIT_PER_SLOT));
		inputs.setRsvpLimitPerPerson(getSlashCommandInput(event, SlashCommandEnum.RSVP_LIMIT_PER_PERSON));

		log.atInfo()
			.setMessage("Slash command received")
			.addKeyValue("inputs", inputs)
			.log();

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
