package dev.m0b1.vrsvp.command;

import dev.m0b1.vrsvp.modal.ModalEnum;
import dev.m0b1.vrsvp.modal.ModalUi;
import dev.m0b1.vrsvp.util.Parser;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import jakarta.annotation.Nonnull;
import java.util.LinkedList;
import java.util.Objects;

@RequiredArgsConstructor
public class SlashCommandReply {

	/**
	 * Build a modal with generated event title and slots for admins to confirm/edit before sending message to channel.
	 */
	public void rsvpCreation(@Nonnull SlashCommandInteractionEvent event, @Nonnull SlashCommandInputs inputs) {

		var description = buildDescription(inputs);
		var modal = ModalUi.editText(ModalEnum.EVENT_CREATION, description);

		event.replyModal(modal).queue();
	}

	/**
	 * Build an RSVP form everyone can see with a given number of time slot, each with an index and incremented timestamp title.
	 */
	private String buildDescription(@Nonnull SlashCommandInputs inputs) {

		var slotDurationInSeconds = inputs.getDurationInMinutes() * 60;
		var startTimestamp = inputs.getStartTimestamp();

		var description = new LinkedList<String>();
		description.add("**New Event**\n");
		description.add(STR."- Starts <t:\{startTimestamp}:R> on <t:\{startTimestamp}:F>");
		description.add(STR."- Each slot is \{inputs.getDurationInMinutes()} minutes long");
		description.add(buildRsvpLimitAddendum(SlashCommandEnum.RSVP_LIMIT_PER_SLOT, inputs.getRsvpLimitPerSlot()));
		description.add(buildRsvpLimitAddendum(SlashCommandEnum.RSVP_LIMIT_PER_PERSON, inputs.getRsvpLimitPerPerson()));
		description.add("");

		for (var slotIndex = 0; slotIndex < inputs.getSlots(); slotIndex++) {
			var slotTimestamp = startTimestamp + (slotDurationInSeconds * slotIndex);
			var line = STR."> #\{slotIndex + 1}\{Parser.SIGNUP_DELIMITER}<t:\{slotTimestamp}:t>";
			description.add(line);
		}

		description.removeIf(Objects::isNull);

		return String.join(Parser.SLOT_DELIMITER, description);
	}

	private String buildRsvpLimitAddendum(SlashCommandEnum slashCommandEnum, Integer limit) {

		if (limit != null) {
			return STR."- \{slashCommandEnum.getDescription()}: \{limit}";
		}

		return null;
	}

}
