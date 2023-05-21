package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.embed.EmbedUi;
import com.mobiusk.vrsvp.command.SlashCommandInputs;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ButtonReplyUnitTest extends TestBase {

	@InjectMocks private ButtonReply reply;

	@Mock private ButtonUi buttonUi;
	@Mock private EmbedUi embedUi;

	private final SlashCommandInputs inputs = new SlashCommandInputs();

	@BeforeEach
	public void beforeEach() {

		when(user.getAsMention()).thenReturn("@Testing");

		when(replyCallbackAction.setEphemeral(anyBoolean())).thenReturn(replyCallbackAction);
		when(replyCallbackAction.addEmbeds(anyCollection())).thenReturn(replyCallbackAction);
		when(replyCallbackAction.addActionRow(anyCollection())).thenReturn(replyCallbackAction);
		when(replyCallbackAction.setComponents(anyCollection())).thenReturn(replyCallbackAction);

		when(buttonInteraction.editMessage(anyString())).thenReturn(messageEditCallbackAction);

		when(buttonInteractionEvent.getUser()).thenReturn(user);
		when(buttonInteractionEvent.editMessage(anyString())).thenReturn(messageEditCallbackAction);
		when(buttonInteractionEvent.getInteraction()).thenReturn(buttonInteraction);
		when(buttonInteractionEvent.reply(anyString())).thenReturn(replyCallbackAction);

		inputs.setSlots(3);
		inputs.setDurationInMinutes(4);
		inputs.setStartTimestamp(5);
	}

	@Test
	void rsvpBuildsEphemeralListOfButtons() {

		var button = Button.primary("test", "Test");
		List<ActionRow> buttonRows = List.of(ActionRow.of(button), ActionRow.of(button));
		when(buttonUi.buildIndexedButtonActionRows(ButtonEnum.RSVP.getId(), inputs.getSlots())).thenReturn(buttonRows);

		reply.rsvpInterest(buttonInteractionEvent);

		verify(buttonInteractionEvent).reply(stringArgumentCaptor.capture());
		verify(buttonUi).buildIndexedButtonActionRows(ButtonEnum.RSVP.getId(), inputs.getSlots());
		verify(replyCallbackAction).setEphemeral(true);
		verify(replyCallbackAction).setComponents(anyCollection());
		verify(replyCallbackAction).queue();

		var expectation = "---\nUse these buttons to toggle your RSVP for any slot.\n---";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void signupToggleAdjustsDescriptionAndEditsEphemeralMessage() {

		when(user.getAsMention()).thenReturn("@Testing");

		reply.rsvpToggle(buttonInteractionEvent, message, 1);

		verify(messageEditAction).queue();
		verify(buttonInteractionEvent).editMessage(stringArgumentCaptor.capture());
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
