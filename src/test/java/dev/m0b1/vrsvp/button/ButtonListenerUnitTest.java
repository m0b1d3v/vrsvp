package dev.m0b1.vrsvp.button;

import dev.m0b1.vrsvp.TestBase;
import dev.m0b1.vrsvp.logging.ServiceLog;
import dev.m0b1.vrsvp.properties.Properties;
import net.dv8tion.jda.api.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ButtonListenerUnitTest extends TestBase {

	@InjectMocks private ButtonListener listener;

	@Mock private ButtonReply reply;

	@Mock private ServiceLog serviceLog;

	@BeforeEach
	public void beforeEach() {
		when(user.getName()).thenReturn("@Testing");
		when(buttonInteractionEvent.getUser()).thenReturn(user);
		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.RSVP.getId());
		when(buttonInteractionEvent.getMember()).thenReturn(member);
		when(member.hasPermission(Permission.ADMINISTRATOR)).thenReturn(true);
	}

	@Test
	void onButtonInteractionFailsIfAccessIsDenied() {

		when(member.hasPermission(Permission.ADMINISTRATOR)).thenReturn(false);

		var checkedButtons = List.of(ButtonEnum.EDIT, ButtonEnum.EDIT_EVENT_ACTIVE, ButtonEnum.EDIT_EVENT_DESCRIPTION);

		for (var checkedButton : checkedButtons) {
			when(buttonInteractionEvent.getComponentId()).thenReturn(checkedButton.getId());
			listener.onButtonInteraction(buttonInteractionEvent);
		}

		verify(reply, times(checkedButtons.size())).ephemeral(buttonInteractionEvent, "Access denied.");
		assertNoRsvpChangesMade();
	}

	@Test
	void onButtonInteractionDoesNotRunPermissionChecksForNonEditButtons() {

		var checkedButtons = List.of(ButtonEnum.RSVP, ButtonEnum.UNKNOWN);

		for (var checkedButton : checkedButtons) {
			when(buttonInteractionEvent.getComponentId()).thenReturn(checkedButton.getId());
			listener.onButtonInteraction(buttonInteractionEvent);
		}

		verify(buttonInteractionEvent, never()).getMember();
	}

	@Test
	void onButtonInteractionFailsIfActionIsNotRecognized() {

		var buttonIds = List.of(ButtonEnum.UNKNOWN.getId(), "testing");

		for (var buttonId : buttonIds) {
			when(buttonInteractionEvent.getComponentId()).thenReturn(buttonId);
			listener.onButtonInteraction(buttonInteractionEvent);
		}

		verify(reply, times(buttonIds.size())).ephemeral(buttonInteractionEvent, "Input not recognized.");
		assertNoRsvpChangesMade();
	}

	@Test
	void onButtonInteractionForEditEventActiveToggleFailsWithoutMessageSource() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.EDIT_EVENT_ACTIVE.getId());
		when(buttonInteractionEvent.getMessage()).thenReturn(null);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply).ephemeral(buttonInteractionEvent, Properties.FORM_NOT_FOUND_REPLY);
		assertNoRsvpChangesMade();
	}

	@Test
	void onButtonInteractionForEditEventDescriptionFailsWithoutMessageSource() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.EDIT_EVENT_DESCRIPTION.getId());
		when(buttonInteractionEvent.getMessage()).thenReturn(null);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply).ephemeral(buttonInteractionEvent, Properties.FORM_NOT_FOUND_REPLY);
		assertNoRsvpChangesMade();
	}

	@Test
	void onButtonInteractionForRsvpToggleFailsWithoutMessageSource() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(STR."\{ButtonEnum.RSVP.getId()}:1");
		when(buttonInteractionEvent.getMessage()).thenReturn(null);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply).ephemeral(buttonInteractionEvent, Properties.FORM_NOT_FOUND_REPLY);
		assertNoRsvpChangesMade();
	}

	@Test
	void onButtonInteractionForRsvpInterestDoesNotCheckMessageSource() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.RSVP.getId());

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(buttonInteractionEvent, never()).getMessage();
		verify(buttonInteractionEvent, never()).getMessageChannel();
	}

	@Test
	void onButtonInteractionForEditInterest() {
		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.EDIT.getId());
		listener.onButtonInteraction(buttonInteractionEvent);
		verify(reply).editInterest(buttonInteractionEvent);
	}

	@Test
	void onButtonInteractionForEditEventActive() {

		setupFetcher(message);
		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.EDIT_EVENT_ACTIVE.getId());
		when(buttonInteractionEvent.getMessage()).thenReturn(message);
		when(buttonInteractionEvent.getMessageChannel()).thenReturn(messageChannel);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply).editToggleRsvpActive(buttonInteractionEvent, message);
	}

	@Test
	void onButtonInteractionForEditEventDescription() {

		setupFetcher(message);
		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.EDIT_EVENT_DESCRIPTION.getId());
		when(buttonInteractionEvent.getMessage()).thenReturn(message);
		when(buttonInteractionEvent.getMessageChannel()).thenReturn(messageChannel);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply).editEventDescription(buttonInteractionEvent, message);
	}

	@Test
	void onButtonInteractionForRsvpInterest() {

		when(buttonInteractionEvent.getComponentId()).thenReturn(ButtonEnum.RSVP.getId());

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply).rsvpInterest(buttonInteractionEvent);
	}

	@Test
	void onButtonInteractionForRsvpToggle() {

		setupFetcher(message);
		when(buttonInteractionEvent.getComponentId()).thenReturn(STR."\{ButtonEnum.RSVP.getId()}:1");
		when(buttonInteractionEvent.getMessage()).thenReturn(message);
		when(buttonInteractionEvent.getMessageChannel()).thenReturn(messageChannel);

		listener.onButtonInteraction(buttonInteractionEvent);

		verify(reply).rsvpToggle(buttonInteractionEvent, message, 1);
	}

	// Test utility method(s)

	private void assertNoRsvpChangesMade() {
		verify(reply, never()).editInterest(buttonInteractionEvent);
		verify(reply, never()).editToggleRsvpActive(buttonInteractionEvent, message);
		verify(reply, never()).editEventDescription(buttonInteractionEvent, message);
		verify(reply, never()).rsvpToggle(eq(buttonInteractionEvent), eq(message), anyInt());
		verify(reply, never()).rsvpInterest(buttonInteractionEvent);
	}

}
