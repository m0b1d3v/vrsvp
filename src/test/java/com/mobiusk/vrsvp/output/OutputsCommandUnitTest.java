package com.mobiusk.vrsvp.output;

import com.mobiusk.vrsvp.TestBase;
import com.mobiusk.vrsvp.input.Inputs;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutputsCommandUnitTest extends TestBase {

	@InjectMocks
	private OutputsCommand output;

	@Mock
	private SlashCommandInteractionEvent event;

	@Mock
	private ReplyCallbackAction callback;

	@Captor
	private ArgumentCaptor<String> stringArgumentCaptor;

	@Captor
	private ArgumentCaptor<List<MessageEmbed>> listMessageEmbedArgumentCaptor;

	private final Inputs inputs = new Inputs();

	@BeforeEach
	public void beforeEach() {

		when(callback.setEphemeral(anyBoolean())).thenReturn(callback);
		when(callback.addEmbeds(anyCollection())).thenReturn(callback);

		when(event.reply(anyString())).thenReturn(callback);

		inputs.setBlocks(2);
		inputs.setSlots(3);
		inputs.setDurationInMinutes(4);
		inputs.setStartTimestamp(5);
	}

	@Test
	void slashCommandReturnsEphemeralValidationMessageIfValidationFails() {

		inputs.setDurationInMinutes(-1);

		output.reply(event, inputs);

		verify(event).reply(stringArgumentCaptor.capture());
		verify(callback).setEphemeral(true);
		verify(callback).queue();

		var expectation = "The minimum duration in minutes for each slot in VRSVP is one minute. Please retry this command with a larger duration.";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void slashCommandReturnsNonEphemeralFormReplyIfValidationPasses() {

		output.reply(event, inputs);

		verify(event).reply(stringArgumentCaptor.capture());
		verify(callback, never()).setEphemeral(true);
		verify(callback).queue();

		var expectation = "---\n**Signups are now available for a new event**\n\nSlots start <t:5:R> on <t:5:F> and each is 4 minute(s) long.\n---";
		assertEquals(expectation, stringArgumentCaptor.getValue());
	}

	@Test
	void buildsEmbedsWithTitles() {

		var embeds = getReplyMessageEmbeds();

		assertEquals(inputs.getBlocks(), embeds.size());

		for (var embedIndex = 0; embedIndex < embeds.size(); embedIndex++) {
			var embed = embeds.get(embedIndex);
			var expected = String.format("Block %d", embedIndex + 1);
			assertEquals(expected, embed.getTitle());
		}
	}

	@Test
	void buildsEmbedFieldsInlineWithNamesAndValues() {

		var embeds = getReplyMessageEmbeds();

		var fields = embeds.stream().flatMap(e -> e.getFields().stream()).toList();

		assertEquals(inputs.getBlocks() * inputs.getSlots(), fields.size());

		for (var fieldIndex = 0; fieldIndex < embeds.size(); fieldIndex++) {

			var field = fields.get(fieldIndex);
			var slotTimestamp = inputs.getStartTimestamp() + (inputs.getDurationInMinutes() * 60 * fieldIndex);
			var expectedName = String.format("#%d - <t:%d:t>", fieldIndex + 1, slotTimestamp);

			assertEquals(expectedName, field.getName());
			assertEquals(OutputsCommand.EMPTY_SLOT_TEXT, field.getValue());
			assertTrue(field.isInline());
		}
	}

	// Test utility methods

	private List<MessageEmbed> getReplyMessageEmbeds() {
		output.reply(event, inputs);
		verify(callback).addEmbeds(listMessageEmbedArgumentCaptor.capture());
		return listMessageEmbedArgumentCaptor.getValue();
	}

}
