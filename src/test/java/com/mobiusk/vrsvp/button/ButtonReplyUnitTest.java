package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.embed.EmbedUi;
import com.mobiusk.vrsvp.input.Inputs;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ButtonReplyUnitTest extends TestBase {

	@InjectMocks private ButtonReply reply;

	@Mock private ButtonUi buttonUi;
	@Mock private EmbedUi embedUi;

	private final Inputs inputs = new Inputs();

	@BeforeEach
	public void beforeEach() {

		when(replyCallbackAction.setEphemeral(anyBoolean())).thenReturn(replyCallbackAction);
		when(replyCallbackAction.addEmbeds(anyCollection())).thenReturn(replyCallbackAction);
		when(replyCallbackAction.addActionRow(anyCollection())).thenReturn(replyCallbackAction);

		when(buttonInteraction.editMessage(anyString())).thenReturn(messageEditCallbackAction);

		when(buttonInteractionEvent.getInteraction()).thenReturn(buttonInteraction);
		when(buttonInteractionEvent.reply(anyString())).thenReturn(replyCallbackAction);

		when(slashCommandInteractionEvent.reply(anyString())).thenReturn(replyCallbackAction);

		inputs.setBlocks(2);
		inputs.setSlots(3);
		inputs.setDurationInMinutes(4);
		inputs.setStartTimestamp(5);
	}

	@Test
	void rsvpBuildsEphemeralListOfButtons() {

		var totalSlots = inputs.getBlocks() * inputs.getSlots();
		List<List<Button>> buttonRows = List.of(Collections.emptyList(), Collections.emptyList());
		when(buttonUi.buildSlotSignupActionRows(totalSlots)).thenReturn(buttonRows);

		reply.rsvp(buttonInteractionEvent, totalSlots);

		verify(buttonInteractionEvent).reply(stringArgumentCaptor.capture());
		verify(buttonUi).buildSlotSignupActionRows(totalSlots);
		verify(replyCallbackAction).setEphemeral(true);
		verify(replyCallbackAction, times(buttonRows.size())).addActionRow(anyCollection());
		verify(replyCallbackAction).queue();

		var expectation = "---\nUse these buttons to toggle your RSVP for any slot.\n---";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void signupToggleAdjustsEmbedAndEditsEphemeralMessage() {

		var userMention = "@Testing";
		var slotIndex = 1;

		reply.signup(buttonInteractionEvent, message, userMention, slotIndex);

		verify(buttonInteractionEvent).getInteraction();
		verify(buttonInteraction).editMessage(stringArgumentCaptor.capture());
		verify(messageEditCallbackAction).queue();

		var expectation = "---\nRSVP state toggled for slot #2\n---";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void ephemeralRepliesAreSent() {

		reply.ephemeral(buttonInteractionEvent, "Testing");

		verify(buttonInteractionEvent).reply("Testing");
		verify(replyCallbackAction).setEphemeral(true);
		verify(replyCallbackAction).queue();
	}

}
