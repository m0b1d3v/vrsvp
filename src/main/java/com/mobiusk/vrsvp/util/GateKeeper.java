package com.mobiusk.vrsvp.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

import javax.annotation.Nonnull;

@UtilityClass
public class GateKeeper {

	public static boolean accessDenied(@Nonnull GenericInteractionCreateEvent event) {
		var member = event.getMember();
		return member != null && ! member.hasPermission(Permission.ADMINISTRATOR);
	}

}
