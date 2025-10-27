package dev.m0b1.vrsvp;

import dev.m0b1.vrsvp.command.SlashCommandInputs;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class TestBase {

	// All JDA mocks
	@Mock protected Button button;
	@Mock protected ButtonInteractionEvent buttonInteractionEvent;
	@Mock protected Channel channel;
	@Mock protected Command command;
	@Mock protected GenericInteractionCreateEvent genericInteractionCreateEvent;
	@Mock protected Guild guild;
	@Mock protected IntegrationPrivilege integrationPrivilege;
	@Mock protected JDA jda;
	@Mock protected Member member;
	@Mock protected Message message;
	@Mock protected MessageChannel messageChannel;
	@Mock protected MessageComponentTree messageComponentTree;
	@Mock protected MessageEditAction messageEditAction;
	@Mock protected MessageEditCallbackAction messageEditCallbackAction;
	@Mock protected MessageEmbed messageEmbed;
	@Mock protected MessageReference messageReference;
	@Mock protected ModalCallbackAction modalCallbackAction;
	@Mock protected ModalInteractionEvent modalInteractionEvent;
	@Mock protected ModalMapping modalMapping;
	@Mock protected ReplyCallbackAction replyCallbackAction;
	@Mock protected RestAction<List<Command>> restActionListCommands;
	@Mock protected RestAction<List<IntegrationPrivilege>> restActionListIntegrationPrivileges;
	@Mock protected RestAction<Message> restActionMessage;
	@Mock protected Role role;
	@Mock protected SlashCommandInteractionEvent slashCommandInteractionEvent;
	@Mock protected User user;

	@Captor protected ArgumentCaptor<Modal> modalArgumentCaptor;
	@Captor protected ArgumentCaptor<SlashCommandInputs> inputsArgumentCaptor;

	protected void setupFetcher(Message output) {
		when(message.getMessageReference()).thenReturn(messageReference);
		when(messageReference.getMessageIdLong()).thenReturn(1L);
		when(messageChannel.retrieveMessageById(anyLong())).thenReturn(restActionMessage);
		when(restActionMessage.onErrorMap(any(Function.class))).thenReturn(restActionMessage);
		when(restActionMessage.complete()).thenReturn(output);
	}

    /**
     * This is purely a function for coverage percentage vanity, do not put any stock in it.
     *
     * @see <a href="https://stackoverflow.com/q/64896125">StackOverflow</a>
     */
    protected void assertUtilityClass(Class<?> utilityClass) throws NoSuchMethodException {

        var constructor = utilityClass.getDeclaredConstructor();
        constructor.setAccessible(true);

        var e1 = assertThrows(InvocationTargetException.class, constructor::newInstance);
        var e2 = e1.getTargetException();

        assertEquals(UnsupportedOperationException.class, e2.getClass());
        assertEquals("This is a utility class and cannot be instantiated", e2.getMessage());
    }

	protected String generateString(int length) {

		var stringBuilder = new StringBuilder(length);

		for (var i = 0; i < length; i++) {
			var alphabetIndex = i % 26;
			var randomCharacter = (char) ('a' + alphabetIndex);
			stringBuilder.append(randomCharacter);
		}

		return stringBuilder.toString();
	}

}
