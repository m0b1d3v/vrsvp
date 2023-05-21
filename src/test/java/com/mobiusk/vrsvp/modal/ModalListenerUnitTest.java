package com.mobiusk.vrsvp.modal;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.util.Formatter;
import net.dv8tion.jda.api.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModalListenerUnitTest extends TestBase {

	@InjectMocks private ModalListener listener;

	@Mock private ModalReply reply;

	@BeforeEach
	public void beforeEach() {
		when(user.getName()).thenReturn("@Testing");
		when(modalInteractionEvent.getUser()).thenReturn(user);
		when(modalInteractionEvent.getModalId()).thenReturn(ModalEnum.EVENT_DESCRIPTION.getId());
		when(modalInteractionEvent.getMember()).thenReturn(member);
		when(member.hasPermission(Permission.ADMINISTRATOR)).thenReturn(true);
	}

	@Test
	void onModalInteractionFailsIfModalIdIsNotRecognizedBeforePermissionsCheck() {

		when(modalInteractionEvent.getModalId()).thenReturn(ModalEnum.UNKNOWN.getId());

		listener.onModalInteraction(modalInteractionEvent);

		verify(reply).ephemeral(modalInteractionEvent, "Input not recognized.");
		verify(modalInteractionEvent, never()).getMember();
	}

	@Test
	void onModalInteractionAccessCanBeDeniedBeforeAnythingIsRead() {

		when(member.hasPermission(Permission.ADMINISTRATOR)).thenReturn(false);

		listener.onModalInteraction(modalInteractionEvent);

		verify(reply).ephemeral(modalInteractionEvent, "Access denied.");
		verify(modalInteractionEvent, never()).getValue(any());
	}

	@Test
	void onModalInteractionFailsIfThereIsNoInputBeforeMessageReferenceFetched() {

		when(modalInteractionEvent.getValue(any())).thenReturn(null);

		listener.onModalInteraction(modalInteractionEvent);

		verify(reply).ephemeral(modalInteractionEvent, "No text input received.");
		verify(modalInteractionEvent, never()).getMessage();
		verify(modalInteractionEvent, never()).getMessageChannel();
	}

	@Test
	void onModalInteractionFailsIfTheMessageSourceCannotBeFetchedBeforeModalEdited() {

		when(modalMapping.getAsString()).thenReturn("Testing");
		when(modalInteractionEvent.getValue(any())).thenReturn(modalMapping);
		when(modalInteractionEvent.getMessage()).thenReturn(null);

		listener.onModalInteraction(modalInteractionEvent);

		verify(reply).ephemeral(modalInteractionEvent, Formatter.FORM_NOT_FOUND_REPLY);
		verify(reply, never()).editEmbedDescriptionFromAdmin(modalInteractionEvent, message, "Testing");
	}

	@Test
	void onModalInteraction() {

		setupFetcher(message);

		when(modalMapping.getAsString()).thenReturn("Testing");
		when(modalInteractionEvent.getValue(any())).thenReturn(modalMapping);
		when(modalInteractionEvent.getMessage()).thenReturn(message);
		when(modalInteractionEvent.getMessageChannel()).thenReturn(messageChannel);

		listener.onModalInteraction(modalInteractionEvent);

		verify(reply).editEmbedDescriptionFromAdmin(modalInteractionEvent, message, "Testing");
	}

}
