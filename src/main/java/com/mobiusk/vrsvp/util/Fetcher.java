package com.mobiusk.vrsvp.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import javax.annotation.Nonnull;

@UtilityClass
@Slf4j
public class Fetcher {

	public static Message getEphemeralMessageSource(Message message, @Nonnull MessageChannel messageChannel) {

		if (message == null) {
			return null;
		}

		var messageReference = message.getMessageReference();
		if (messageReference == null) {
			return null;
		}

		return messageChannel
			.retrieveMessageById(messageReference.getMessageIdLong())
			.onErrorMap(ex -> {
				log.warn("Message retrieval attempted on deleted message", ex);
				return null;
			})
			.complete();
	}

}
