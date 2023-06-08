package com.mobiusk.vrsvp.modal;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModalReplyUnitTest extends TestBase {

	@InjectMocks private ModalReply reply;

	@BeforeEach
	public void beforeEach() {

		when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
		when(message.editMessageEmbeds(any(MessageEmbed[].class))).thenReturn(messageEditAction);

		when(modalInteractionEvent.reply(anyString())).thenReturn(replyCallbackAction);
		when(modalInteractionEvent.editMessage(anyString())).thenReturn(messageEditCallbackAction);

		when(replyCallbackAction.setEphemeral(anyBoolean())).thenReturn(replyCallbackAction);
		when(replyCallbackAction.addEmbeds(any(MessageEmbed[].class))).thenReturn(replyCallbackAction);
		when(replyCallbackAction.addActionRow(anyCollection())).thenReturn(replyCallbackAction);
	}

	@Test
	void createEmbedFormFromAdminAddsNecessaryComponents() {

		reply.createRsvpFromAdmin(modalInteractionEvent, "Testing");

		verify(modalInteractionEvent).reply("Testing");
		verify(replyCallbackAction).addActionRow(anyCollection());
		verify(replyCallbackAction).queue();
	}

	@Test
	void editEventDescriptionFromAdminEditsMessageEmbeds() {

		reply.editEmbedDescriptionFromAdmin(modalInteractionEvent, message, "Testing");

		verify(message).editMessageEmbeds(any(MessageEmbed.class));
		verify(messageEditAction).queue();
	}

	@Test
	void editEventDescriptionFromAdminEditsOriginalMessage() {

		reply.editEmbedDescriptionFromAdmin(modalInteractionEvent, message, "Testing");

		verify(modalInteractionEvent).editMessage("Description has been updated.");
		verify(messageEditCallbackAction).queue();
	}

	@Test
	void ephemeral() {

		reply.ephemeral(modalInteractionEvent, "Testing");

		verify(modalInteractionEvent).reply("Testing");
		verify(replyCallbackAction).setEphemeral(true);
		verify(replyCallbackAction).queue();
	}

}
