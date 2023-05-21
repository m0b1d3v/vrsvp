package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ButtonListenerUnitTest extends TestBase {

	@InjectMocks private ButtonListener listener;

	@Mock private ButtonReply reply;

	@BeforeEach
	public void beforeEach() {

		when(message.getMessageReference()).thenReturn(messageReference);

		when(messageReference.getMessageIdLong()).thenReturn(1L);

		when(messageChannel.retrieveMessageById(anyLong())).thenReturn(messageRestAction);

		when(messageRestAction.onErrorMap(any(Function.class))).thenReturn(messageRestAction);
		when(messageRestAction.complete()).thenReturn(message);

		when(buttonInteractionEvent.getUser()).thenReturn(user);
		when(buttonInteractionEvent.getMessage()).thenReturn(message);
		when(buttonInteractionEvent.getMessageChannel()).thenReturn(messageChannel);
	}

	@Test
	void unexpectedButtonInteractionsReceiveErrorMessage() {

		when(buttonInteractionEvent.getComponentId()).thenReturn("testing");

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply).ephemeral(buttonInteractionEvent, "Input not recognized.");
		verify(reply, never()).rsvpInterest(eq(buttonInteractionEvent));
		verify(reply, never()).rsvpToggle(eq(buttonInteractionEvent), any(), anyInt());
	}

	@Test
	void buttonInteractionEventHandledForEditInterest() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.EDIT.getId());

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply).edit(buttonInteractionEvent);
	}

	@Test
	void buttonInteractionEventWithoutMessageReferencesFailsForEditDescriptionInterest() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.EDIT_EVENT_DESCRIPTION.getId());
		when(message.getMessageReference()).thenReturn(null);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply, never()).editEventDescription(buttonInteractionEvent, message);
		verify(buttonInteractionEvent, never()).getMessageChannel();
	}

	@Test
	void buttonInteractionEventWithoutMessageSourceFailsForEditDescriptionInterest() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.EDIT_EVENT_DESCRIPTION.getId());
		when(messageRestAction.complete()).thenReturn(null);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply, never()).editEventDescription(buttonInteractionEvent, message);
	}

	@Test
	void buttonInteractionEventHandledForEditDescriptionInterest() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.EDIT_EVENT_DESCRIPTION.getId());

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply).editEventDescription(buttonInteractionEvent, message);
	}

	@Test
	void buttonInteractionEventHandledForRsvpInterest() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.RSVP.getId());

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply, never()).ephemeral(buttonInteractionEvent, "Input not recognized.");
		verify(reply).rsvpInterest(buttonInteractionEvent);
		verify(reply, never()).rsvpToggle(eq(buttonInteractionEvent), any(), anyInt());
	}

	@Test
	void buttonInteractionEventForSignupButtonWithContextIdButNoMessageSourceDoesNotToggleSlots() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.RSVP.getId() + ":1");
		when(message.getMessageReference()).thenReturn(null);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply, never()).ephemeral(buttonInteractionEvent, "Input not recognized.");
		verify(reply, never()).rsvpInterest(eq(buttonInteractionEvent));
		verify(reply, never()).rsvpToggle(eq(buttonInteractionEvent), any(), anyInt());

		verify(message).getMessageReference();
		verify(buttonInteractionEvent, never()).getMessageChannel();
	}

	@Test
	void buttonInteractionEventForSignupButtonWithContextIdAndMessageSourceTogglesSlots() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.RSVP.getId() + ":1");
		when(user.getAsMention()).thenReturn("@Testing");

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply, never()).ephemeral(buttonInteractionEvent, "Input not recognized.");
		verify(reply, never()).rsvpInterest(eq(buttonInteractionEvent));
		verify(reply).rsvpToggle(buttonInteractionEvent, message, 1);

		verify(message).getMessageReference();
		verify(buttonInteractionEvent).getMessageChannel();
		verify(messageChannel).retrieveMessageById(1L);
		verify(messageRestAction).complete();
	}

}
