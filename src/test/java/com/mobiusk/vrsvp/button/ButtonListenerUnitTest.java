package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

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

		when(messageEmbed.getFields()).thenReturn(List.of(messageEmbedField, messageEmbedField, messageEmbedField));

		when(message.getEmbeds()).thenReturn(List.of(messageEmbed, messageEmbed));
		when(message.getMessageReference()).thenReturn(messageReference);

		when(messageReference.getMessageIdLong()).thenReturn(1L);

		when(messageChannelUnion.retrieveMessageById(anyLong())).thenReturn(messageRestAction);

		when(messageRestAction.complete()).thenReturn(message);

		when(buttonInteractionEvent.getUser()).thenReturn(user);
		when(buttonInteractionEvent.getMessage()).thenReturn(message);
		when(buttonInteractionEvent.getChannel()).thenReturn(messageChannelUnion);
	}

	@Test
	void unexpectedButtonInteractionsReceiveErrorMessage() {

		when(buttonInteractionEvent.getComponentId()).thenReturn("testing");

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply).ephemeral(buttonInteractionEvent, "Input not recognized.");
		verify(reply, never()).rsvp(eq(buttonInteractionEvent), anyInt());
		verify(reply, never()).signup(eq(buttonInteractionEvent), any(), any(), anyInt());
	}

	@Test
	void buttonInteractionEventHandledForRsvpInterest() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonUi.RSVP);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply, never()).ephemeral(buttonInteractionEvent, "Input not recognized.");
		verify(reply).rsvp(buttonInteractionEvent, messageEmbed.getFields().size() * message.getEmbeds().size());
		verify(reply, never()).signup(eq(buttonInteractionEvent), any(), any(), anyInt());
	}

	@Test
	void buttonInteractionEventForSignupButtonWithoutContextIdDoesNotGetButtonEventSourceOrToggleSlots() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonUi.SIGNUP);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply, never()).ephemeral(buttonInteractionEvent, "Input not recognized.");
		verify(reply, never()).rsvp(eq(buttonInteractionEvent), anyInt());
		verify(reply, never()).signup(eq(buttonInteractionEvent), any(), any(), anyInt());

		verify(message, never()).getMessageReference();
		verify(buttonInteractionEvent, never()).getChannel();
	}

	@Test
	void buttonInteractionEventForSignupButtonWithContextIdButNoMessageSourceDoesNotToggleSlots() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonUi.SIGNUP + ":1");
		when(message.getMessageReference()).thenReturn(null);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply, never()).ephemeral(buttonInteractionEvent, "Input not recognized.");
		verify(reply, never()).rsvp(eq(buttonInteractionEvent), anyInt());
		verify(reply, never()).signup(eq(buttonInteractionEvent), any(), any(), anyInt());

		verify(message).getMessageReference();
		verify(buttonInteractionEvent, never()).getChannel();
	}

	@Test
	void buttonInteractionEventForSignupButtonWithContextIdAndMessageSourceTogglesSlots() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonUi.SIGNUP + ":1");
		when(user.getAsMention()).thenReturn("@Testing");

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply, never()).ephemeral(buttonInteractionEvent, "Input not recognized.");
		verify(reply, never()).rsvp(eq(buttonInteractionEvent), anyInt());
		verify(reply).signup(buttonInteractionEvent, message, "@Testing", 1);

		verify(message).getMessageReference();
		verify(buttonInteractionEvent).getChannel();
		verify(messageChannelUnion).retrieveMessageById(1L);
		verify(messageRestAction).complete();
		verify(buttonInteractionEvent).getUser();
		verify(user).getAsMention();
	}

}
