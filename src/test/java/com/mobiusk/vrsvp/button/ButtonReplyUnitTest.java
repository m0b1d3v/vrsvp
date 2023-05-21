package com.mobiusk.vrsvp.button;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.command.SlashCommandEnum;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ButtonReplyUnitTest extends TestBase {

	@InjectMocks private ButtonReply reply;

	@BeforeEach
	public void beforeEach() {

		when(user.getName()).thenReturn("@Testing");
		when(user.getAsMention()).thenReturn("@Testing");

		when(buttonInteractionEvent.getUser()).thenReturn(user);
		when(buttonInteractionEvent.getMessage()).thenReturn(message);
		when(buttonInteractionEvent.reply(anyString())).thenReturn(replyCallbackAction);
		when(buttonInteractionEvent.editMessage(anyString())).thenReturn(messageEditCallbackAction);
		when(buttonInteractionEvent.replyModal(any())).thenReturn(modalCallbackAction);

		when(replyCallbackAction.setEphemeral(true)).thenReturn(replyCallbackAction);
		when(replyCallbackAction.setComponents(anyCollection())).thenReturn(replyCallbackAction);
		when(replyCallbackAction.addActionRow(anyCollection())).thenReturn(replyCallbackAction);

		when(message.editMessageEmbeds(any(MessageEmbed.class))).thenReturn(messageEditAction);
		when(message.editMessageComponents(any(ActionRow.class))).thenReturn(messageEditAction);
		when(message.getEmbeds()).thenReturn(List.of(messageEmbed));
		when(messageEmbed.getDescription()).thenReturn("> #1");
	}

	@Test
	void editInterestBuildsEphemeralActionPrompts() {

		reply.editInterest(buttonInteractionEvent);

		var expectedReply = "Use these buttons to edit the RSVP form.\nWhen editing descriptions, it is suggested to copy the text into your favorite editor for making changes.";

		verify(buttonInteractionEvent).reply(expectedReply);
		verify(replyCallbackAction).setEphemeral(true);
		verify(replyCallbackAction).addActionRow(anyCollection());
		verify(replyCallbackAction).queue();
	}

	@Test
	void editToggleRsvpActiveFailsIfButtonNotFoundWithoutEditingMessageComponents() {

		when(message.getButtonById(ButtonEnum.RSVP.getId())).thenReturn(null);

		reply.editToggleRsvpActive(buttonInteractionEvent, message);

		verify(buttonInteractionEvent).editMessage("Could not toggle RSVP button");
		verify(messageEditCallbackAction).queue();
		verify(message, never()).editMessageComponents(any(ActionRow.class));
	}

	@Test
	void editToggleRsvpActiveChangesStateOfFirstButton() {

		var button = Button.primary("test", "Testing");
		var notToggled = Button.secondary("test2", "Testing 2");

		when(message.getButtonById(ButtonEnum.RSVP.getId())).thenReturn(button);
		when(message.getButtons()).thenReturn(List.of(button, notToggled));

		reply.editToggleRsvpActive(buttonInteractionEvent, message);

		verify(buttonInteractionEvent).editMessage("RSVP button toggled");
		verify(messageEditCallbackAction).queue();
		verify(message).editMessageComponents(actionRowArgumentCaptor.capture());
		verify(messageEditAction).queue();

		var buttons = actionRowArgumentCaptor.getValue().getButtons();
		assertEquals(2, buttons.size());
		assertNotEquals(button.isDisabled(), buttons.get(0).isDisabled());
		assertEquals(notToggled.isDisabled(), buttons.get(1).isDisabled());
	}

	@Test
	void editEventDescription() {
		reply.editEventDescription(buttonInteractionEvent, message);
		verify(buttonInteractionEvent).replyModal(any());
		verify(modalCallbackAction).queue();
	}

	@Test
	void rsvpInterest() {

		reply.rsvpInterest(buttonInteractionEvent);

		verify(buttonInteractionEvent).reply("Use these buttons to toggle your RSVP for any slot.");
		verify(replyCallbackAction).setEphemeral(true);
		verify(replyCallbackAction).setComponents(anyCollection());
		verify(replyCallbackAction).queue();
	}

	@Test
	void rsvpToggleFailsIfAddingUserMentionAndRsvpLimitPerPersonExceededWithoutEditingDescription() {

		var rule = SlashCommandEnum.RSVP_LIMIT_PER_PERSON.getDescription() + ": 0";
		when(messageEmbed.getDescription()).thenReturn(rule + "\n> #1");

		reply.rsvpToggle(buttonInteractionEvent, message, 0);

		verify(buttonInteractionEvent).editMessage("Signup limit exceeded, cannot RSVP for slot #1");
		verify(messageEditCallbackAction).queue();
		verify(message, never()).editMessageEmbeds(any(MessageEmbed.class));
	}

	@Test
	void rsvpToggleFailsIfAddingUserMentionAndRsvpLimitPerSlotExceededWithoutEditingDescription() {

		var rule = SlashCommandEnum.RSVP_LIMIT_PER_SLOT.getDescription() + ": 0";
		when(messageEmbed.getDescription()).thenReturn(rule + "\n> #1\n> #2");

		reply.rsvpToggle(buttonInteractionEvent, message, 1);

		verify(buttonInteractionEvent).editMessage("Signup limit exceeded, cannot RSVP for slot #2");
		verify(messageEditCallbackAction).queue();
		verify(message, never()).editMessageEmbeds(any(MessageEmbed.class));
	}

	@Test
	void rsvpToggleSucceedsIfRemovingUserMentionWhenRsvpLimitPerPersonExceeded() {

		var rule = SlashCommandEnum.RSVP_LIMIT_PER_PERSON.getDescription() + ": 0";
		when(messageEmbed.getDescription()).thenReturn(rule + "\n> #1, @Testing");

		reply.rsvpToggle(buttonInteractionEvent, message, 0);

		verify(buttonInteractionEvent).editMessage("RSVP state toggled for slot #1");
		verify(messageEditCallbackAction).queue();
		verify(message).editMessageEmbeds(any(MessageEmbed.class));
		verify(messageEditAction).queue();
	}

	@Test
	void rsvpToggleSucceedsIfRemovingUserMentionWhenRsvpLimitPerSlotExceeded() {

		var rule = SlashCommandEnum.RSVP_LIMIT_PER_SLOT.getDescription() + ": 0";
		when(messageEmbed.getDescription()).thenReturn(rule + "\n> #1\n> #2, @Testing");

		reply.rsvpToggle(buttonInteractionEvent, message, 1);

		verify(buttonInteractionEvent).editMessage("RSVP state toggled for slot #2");
		verify(messageEditCallbackAction).queue();
		verify(message).editMessageEmbeds(any(MessageEmbed.class));
		verify(messageEditAction).queue();
	}

	@Test
	void rsvpToggle() {

		reply.rsvpToggle(buttonInteractionEvent, message, 0);

		verify(buttonInteractionEvent).editMessage("RSVP state toggled for slot #1");
		verify(messageEditCallbackAction).queue();
		verify(message).editMessageEmbeds(any(MessageEmbed.class));
		verify(messageEditAction).queue();
	}

	@Test
	void ephemeral() {

		reply.ephemeral(buttonInteractionEvent, "Testing");

		verify(buttonInteractionEvent).reply("Testing");
		verify(replyCallbackAction).setEphemeral(true);
		verify(replyCallbackAction).queue();
	}

}
