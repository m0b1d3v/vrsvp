package dev.m0b1.vrsvp.command;

import dev.m0b1.vrsvp.logging.LogData;
import dev.m0b1.vrsvp.logging.ServiceLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.event.Level;

import javax.annotation.Nonnull;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class SlashCommandListener extends ListenerAdapter {

	// Class constructor field(s)
	private final ServiceLog serviceLog;
	private final SlashCommandReply reply;

	/**
	 * When slash command is submitted to create an RSVP, the magic will happen here.
	 */
	@Override
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		if ( ! SlashCommandUi.INVOCATION.equals(event.getName())) {

			serviceLog.run(LogData.builder()
				.level(Level.WARN)
				.message("Unrecognized slash command received")
				.event(event)
				.markers(Map.of("eventName", event.getName()))
			);;

			return;
		}

		handleSlashCommandInteraction(event);
	}

	private void handleSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {

		var inputs = new SlashCommandInputs();
		inputs.setDurationInMinutes(getSlashCommandInput(event, SlashCommandEnum.DURATION));
		inputs.setSlots(getSlashCommandInput(event, SlashCommandEnum.SLOTS));
		inputs.setStartTimestamp(getSlashCommandInput(event, SlashCommandEnum.START));
		inputs.setRsvpLimitPerSlot(getSlashCommandInput(event, SlashCommandEnum.RSVP_LIMIT_PER_SLOT));
		inputs.setRsvpLimitPerPerson(getSlashCommandInput(event, SlashCommandEnum.RSVP_LIMIT_PER_PERSON));

		serviceLog.run(LogData.builder()
			.level(Level.INFO)
			.message("Slash command received")
			.event(event)
			.markers(Map.of("inputs", inputs))
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
