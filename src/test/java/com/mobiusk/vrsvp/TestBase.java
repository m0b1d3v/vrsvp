package com.mobiusk.vrsvp;

import com.mobiusk.vrsvp.input.Inputs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class TestBase {

	// All JDA mocks
	@Mock protected ButtonInteraction buttonInteraction;
	@Mock protected ButtonInteractionEvent buttonInteractionEvent;
	@Mock protected Message message;
	@Mock protected MessageChannel messageChannel;
	@Mock protected MessageEditAction messageEditAction;
	@Mock protected MessageEditCallbackAction messageEditCallbackAction;
	@Mock protected MessageEmbed messageEmbed;
	@Mock protected MessageEmbed.Field messageEmbedField;
	@Mock protected MessageReference messageReference;
	@Mock protected ReplyCallbackAction replyCallbackAction;
	@Mock protected RestAction<Message> messageRestAction;
	@Mock protected SlashCommandInteractionEvent slashCommandInteractionEvent;
	@Mock protected User user;

	@Captor protected ArgumentCaptor<Inputs> inputsArgumentCaptor;
	@Captor protected ArgumentCaptor<List<Long>> listLongArgumentCaptor;
	@Captor protected ArgumentCaptor<String> stringArgumentCaptor;

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

}
