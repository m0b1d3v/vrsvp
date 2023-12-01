package dev.m0b1.vrsvp.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import javax.annotation.Nonnull;

@UtilityClass
@Slf4j
public class Fetcher {

	/**
	 * Given a (usually ephemeral) message, retrieve the original message it references within the same channel.
	 */
	public static Message getEphemeralMessageSource(Message message, @Nonnull MessageChannel messageChannel) {

		if (message == null) {
			return null;
		}

		var messageReference = message.getMessageReference();
		if (messageReference == null) {
			return null;
		}

		try {
			return messageChannel
				.retrieveMessageById(messageReference.getMessageIdLong())
				.onErrorMap(exception -> {
					// The most common cause of this would be users clicking ephemeral signup buttons for deleted posts
					log.warn(exception.getMessage());
					return null;
				})
				.complete();
		} catch (InsufficientPermissionException exception) {
			// This usually happens when server owners add the bot for the first time but forget to give it permissions
			// The calling method for this function will log where it is happening for us, no action necessary here
			log.warn(exception.getMessage());
			return null;
		}
	}

}
