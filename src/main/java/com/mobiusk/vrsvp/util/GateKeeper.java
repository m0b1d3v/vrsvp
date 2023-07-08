package com.mobiusk.vrsvp.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@UtilityClass
public class GateKeeper {

	/**
	 * Is the member who caused this event allowed access to it?
	 */
	public static boolean accessDenied(@Nonnull GenericInteractionCreateEvent event) {
		return notAdmin(event) && guildIntegrationPrivilegesDoNotAllow(event);
	}

	private static boolean notAdmin(GenericInteractionCreateEvent event) {
		var member = event.getMember();
		return member != null && ! member.hasPermission(Permission.ADMINISTRATOR);
	}

	private static boolean guildIntegrationPrivilegesDoNotAllow(GenericInteractionCreateEvent event) {

		var denied = true;
		var guild = event.getGuild();

		if (guild != null) {
			var privileges = fetchGuildIntegrationPrivileges(guild);
			for (var privilege : privileges) {
				if (privilegeApplies(privilege, event)) {
					denied = false;
					break;
				}
			}
		}

		return denied;
	}

	private static List<IntegrationPrivilege> fetchGuildIntegrationPrivileges(Guild guild) {

		var result = new ArrayList<IntegrationPrivilege>();

		guild.getJDA()
			.retrieveCommands()
			.onErrorMap(exception -> Collections.emptyList())
			.complete()
			.stream()
			.filter(c -> "vrsvp".equals(c.getName()))
			.findFirst()
			.ifPresent(command -> {

				// Errors almost every single time, because most servers don't have command integration privileges set
				var privileges = command.retrievePrivileges(guild)
					.onErrorMap(exception -> Collections.emptyList())
					.complete();

				result.addAll(privileges);
			});

		return result;
	}

	private static boolean privilegeApplies(IntegrationPrivilege privilege, GenericInteractionCreateEvent event) {

		var id = privilege.getId();
		var type = privilege.getType();

		return privilege.isEnabled() && (
			(IntegrationPrivilege.Type.ROLE.equals(type) && userHasRole(id, event))
			|| (IntegrationPrivilege.Type.USER.equals(type) && userMatches(id, event))
			|| (IntegrationPrivilege.Type.CHANNEL.equals(type) && channelMatches(id, event))
		);
	}

	private static boolean userHasRole(String id, GenericInteractionCreateEvent event) {

		var member = event.getMember();
		if (member != null) {
			return member.getRoles().stream().anyMatch(role -> id.equals(role.getId()));
		}

		return false;
	}

	private static boolean userMatches(String id, GenericInteractionCreateEvent event) {

		var member = event.getMember();
		if (member != null) {
			return id.equals(member.getId());
		}

		return false;
	}

	private static boolean channelMatches(String id, GenericInteractionCreateEvent event) {

		var channel = event.getChannel();
		if (channel != null) {
			return id.equals(channel.getId());
		}

		return false;
	}

}
