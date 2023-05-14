package com.mobiusk.vrsvp;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@UtilityClass
public class DiscordBotCommands {

	public static final String SLASH = "vrsvp";

	public static SlashCommandData slash() {
		return Commands.slash(SLASH, "Create virtual RSVP")
			.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
	}

}
