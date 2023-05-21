package com.mobiusk.vrsvp.command;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SlashCommandReplyUnitTest extends TestBase {

	@InjectMocks private SlashCommandReply reply;

	private final SlashCommandInputs inputs = new SlashCommandInputs();

	@Test
	void rsvpCreationRunsEmbedAndButtonUi() {

		inputs.setStartTimestamp(1);
		inputs.setSlots(2);
		inputs.setDurationInMinutes(20);

		when(slashCommandInteractionEvent.reply(anyString())).thenReturn(replyCallbackAction);
		when(replyCallbackAction.addEmbeds(any(MessageEmbed[].class))).thenReturn(replyCallbackAction);
		when(replyCallbackAction.addActionRow(anyCollection())).thenReturn(replyCallbackAction);

		reply.rsvpCreation(slashCommandInteractionEvent, inputs);

		verify(slashCommandInteractionEvent).reply("");
		verify(replyCallbackAction).addEmbeds(any(MessageEmbed.class));
		verify(replyCallbackAction).addActionRow(anyCollection());
		verify(replyCallbackAction).queue();
	}

}
