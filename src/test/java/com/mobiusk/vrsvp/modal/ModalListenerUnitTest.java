package com.mobiusk.vrsvp.modal;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.util.Formatter;
import net.dv8tion.jda.api.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModalListenerUnitTest extends TestBase {

	@InjectMocks private ModalListener listener;

	@Mock private ModalReply reply;

	private static final List<ModalEnum> VALID_MODAL_ENUMS = List.of(ModalEnum.EVENT_CREATION, ModalEnum.EVENT_DESCRIPTION);

	@BeforeEach
	public void beforeEach() {
		when(user.getName()).thenReturn("@Testing");
		when(modalInteractionEvent.getUser()).thenReturn(user);
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

		for (var modalId : VALID_MODAL_ENUMS) {

			when(modalInteractionEvent.getModalId()).thenReturn(modalId.getId());
			when(member.hasPermission(Permission.ADMINISTRATOR)).thenReturn(false);

			listener.onModalInteraction(modalInteractionEvent);
		}

		verify(reply, times(VALID_MODAL_ENUMS.size())).ephemeral(modalInteractionEvent, "Access denied.");
		verify(modalInteractionEvent, never()).getValue(any());
	}

	@Test
	void onModalInteractionFailsIfThereIsNoInput() {

		for (var modalId : VALID_MODAL_ENUMS) {

			when(modalInteractionEvent.getModalId()).thenReturn(modalId.getId());
			when(modalInteractionEvent.getValue(any())).thenReturn(null);

			listener.onModalInteraction(modalInteractionEvent);
		}

		verify(reply, times(VALID_MODAL_ENUMS.size())).ephemeral(modalInteractionEvent, "No text input received.");
		verify(modalInteractionEvent, never()).getMessage();
		verify(modalInteractionEvent, never()).getMessageChannel();
	}

	@Test
	void onModalInteractionFailsIfTheMessageSourceCannotBeFetchedBeforeEventEdited() {

		when(modalMapping.getAsString()).thenReturn("Testing");
		when(modalInteractionEvent.getModalId()).thenReturn(ModalEnum.EVENT_DESCRIPTION.getId());
		when(modalInteractionEvent.getValue(any())).thenReturn(modalMapping);
		when(modalInteractionEvent.getMessage()).thenReturn(null);

		listener.onModalInteraction(modalInteractionEvent);

		verify(reply).ephemeral(modalInteractionEvent, Formatter.FORM_NOT_FOUND_REPLY);
		verify(reply, never()).createEmbedFormFromAdmin(modalInteractionEvent, "Testing");
		verify(reply, never()).editEmbedDescriptionFromAdmin(modalInteractionEvent, message, "Testing");
	}

	@Test
	void onModalInteractionForEventCreate() {

		setupFetcher(message);

		when(modalMapping.getAsString()).thenReturn("Testing");
		when(modalInteractionEvent.getModalId()).thenReturn(ModalEnum.EVENT_CREATION.getId());
		when(modalInteractionEvent.getValue(any())).thenReturn(modalMapping);
		when(modalInteractionEvent.getMessage()).thenReturn(message);
		when(modalInteractionEvent.getMessageChannel()).thenReturn(messageChannel);

		listener.onModalInteraction(modalInteractionEvent);

		verify(reply).createEmbedFormFromAdmin(modalInteractionEvent, "Testing");
		verify(reply, never()).editEmbedDescriptionFromAdmin(modalInteractionEvent, message, "Testing");
	}

	@Test
	void onModalInteractionForEventEdit() {

		setupFetcher(message);

		when(modalMapping.getAsString()).thenReturn("Testing");
		when(modalInteractionEvent.getModalId()).thenReturn(ModalEnum.EVENT_DESCRIPTION.getId());
		when(modalInteractionEvent.getValue(any())).thenReturn(modalMapping);
		when(modalInteractionEvent.getMessage()).thenReturn(message);
		when(modalInteractionEvent.getMessageChannel()).thenReturn(messageChannel);

		listener.onModalInteraction(modalInteractionEvent);

		verify(reply, never()).createEmbedFormFromAdmin(modalInteractionEvent, "Testing");
		verify(reply).editEmbedDescriptionFromAdmin(modalInteractionEvent, message, "Testing");
	}

}
