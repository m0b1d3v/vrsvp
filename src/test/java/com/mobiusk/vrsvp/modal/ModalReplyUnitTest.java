package com.mobiusk.vrsvp.modal;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModalReplyUnitTest extends TestBase {

	@InjectMocks private ModalReply reply;

	@BeforeEach
	public void beforeEach() {
		when(interactionHook.editOriginal(anyString())).thenReturn(webhookMessageEditActionMessage);
		when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
		when(message.editMessageEmbeds(any(MessageEmbed[].class))).thenReturn(messageEditAction);
		when(modalInteractionEvent.getHook()).thenReturn(interactionHook);
		when(modalInteractionEvent.reply(anyString())).thenReturn(replyCallbackAction);
		when(replyCallbackAction.setEphemeral(anyBoolean())).thenReturn(replyCallbackAction);
	}

	@Test
	void editEventDescriptionFromAdminEditsMessageEmbeds() {

		reply.editEmbedDescriptionFromAdmin(modalInteractionEvent, message, "Testing");

		verify(message).editMessageEmbeds(any(MessageEmbed.class));
		verify(messageEditAction).queue();
	}

	@Test
	void editEventDescriptionFromAdminEditsOriginalEventHook() {

		reply.editEmbedDescriptionFromAdmin(modalInteractionEvent, message, "Testing");

		verify(modalInteractionEvent).getHook();
		verify(interactionHook).editOriginal("Description has been updated.");
		verify(webhookMessageEditActionMessage).queue();
	}

	@Test
	void ephemeral() {

		reply.ephemeral(modalInteractionEvent, "Testing");

		verify(modalInteractionEvent).reply("Testing");
		verify(replyCallbackAction).setEphemeral(true);
		verify(replyCallbackAction).queue();
	}

}
