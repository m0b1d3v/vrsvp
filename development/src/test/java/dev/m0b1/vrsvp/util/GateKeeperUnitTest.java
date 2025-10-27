package dev.m0b1.vrsvp.util;

import dev.m0b1.vrsvp.TestBase;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GateKeeperUnitTest extends TestBase {

	@BeforeEach
	void beforeEach() {

		when(genericInteractionCreateEvent.getMember()).thenReturn(member);
		when(genericInteractionCreateEvent.getGuild()).thenReturn(guild);

		when(member.hasPermission(Permission.ADMINISTRATOR)).thenReturn(false);

		when(guild.getJDA()).thenReturn(jda);
		when(jda.retrieveCommands()).thenReturn(restActionListCommands);

		when(restActionListCommands.onErrorMap(any())).thenReturn(restActionListCommands);
		when(restActionListCommands.complete()).thenReturn(Collections.emptyList());

		when(restActionListIntegrationPrivileges.onErrorMap(any())).thenReturn(restActionListIntegrationPrivileges);
		when(restActionListIntegrationPrivileges.complete()).thenReturn(Collections.emptyList());
	}

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(GateKeeper.class);
	}

	@Test
	void permissionGrantedIfEventHasNoMember() {
		when(genericInteractionCreateEvent.getMember()).thenReturn(null);
		assertFalse(GateKeeper.accessDenied(genericInteractionCreateEvent));
	}

	@Test
	void permissionGrantedIfEventMemberExistsAndIsAdministrator() {
		when(member.hasPermission(Permission.ADMINISTRATOR)).thenReturn(true);
		assertFalse(GateKeeper.accessDenied(genericInteractionCreateEvent));
	}

	@Test
	void permissionDeniedIfNotAdministratorAndNoIntegrationPrivileges() {

		assertTrue(GateKeeper.accessDenied(genericInteractionCreateEvent));

		verify(genericInteractionCreateEvent).getMember();
		verify(genericInteractionCreateEvent).getGuild();
	}

	@Test
	void integrationPrivilegesForVrsvpCommandNotFound() {

		var command = setupMockCommand("not-vrsvp");

		assertTrue(GateKeeper.accessDenied(genericInteractionCreateEvent));

		verify(command, never()).retrievePrivileges(guild);
	}

	@Test
	void integrationPrivilegesForVrsvpCommandNotSet() {

		var command = setupMockCommand("vrsvp");

		assertTrue(GateKeeper.accessDenied(genericInteractionCreateEvent));

		verify(command).retrievePrivileges(guild);
	}

	@Test
	void permissionDeniedIfIntegrationPrivilegeNoMatchOnChannel() {

		setupMockCommand("vrsvp");
		setupMockIntegrationPrivilege(IntegrationPrivilege.Type.CHANNEL);

		when(genericInteractionCreateEvent.getChannel()).thenReturn(null);

		assertTrue(GateKeeper.accessDenied(genericInteractionCreateEvent));
	}

	@Test
	void permissionGrantedIfIntegrationPrivilegeMatchOnChannel() {

		setupMockCommand("vrsvp");
		setupMockIntegrationPrivilege(IntegrationPrivilege.Type.CHANNEL);

		when(channel.getId()).thenReturn("test");
		when(genericInteractionCreateEvent.getChannel()).thenReturn(channel);

		assertFalse(GateKeeper.accessDenied(genericInteractionCreateEvent));
	}

	@Test
	void permissionDeniedIfIntegrationPrivilegeNoMatchOnUser() {

		setupMockCommand("vrsvp");
		setupMockIntegrationPrivilege(IntegrationPrivilege.Type.USER);

		// First return to get us past admin check, second return to trip conditional inside privileges
		when(genericInteractionCreateEvent.getMember()).thenReturn(member).thenReturn(null);

		assertTrue(GateKeeper.accessDenied(genericInteractionCreateEvent));
	}

	@Test
	void permissionGrantedIfIntegrationPrivilegeMatchOnUser() {

		setupMockCommand("vrsvp");
		setupMockIntegrationPrivilege(IntegrationPrivilege.Type.USER);

		when(member.getId()).thenReturn("test");
		when(genericInteractionCreateEvent.getMember()).thenReturn(member);

		assertFalse(GateKeeper.accessDenied(genericInteractionCreateEvent));
	}

	@Test
	void permissionDeniedIfIntegrationPrivilegeNoMatchOnRole() {

		setupMockCommand("vrsvp");
		setupMockIntegrationPrivilege(IntegrationPrivilege.Type.ROLE);

		// First return to get us past admin check, second return to trip conditional inside privileges
		when(genericInteractionCreateEvent.getMember()).thenReturn(member).thenReturn(null);

		assertTrue(GateKeeper.accessDenied(genericInteractionCreateEvent));
	}

	@Test
	void permissionGrantedIfIntegrationPrivilegeMatchOnRole() {

		setupMockCommand("vrsvp");
		setupMockIntegrationPrivilege(IntegrationPrivilege.Type.ROLE);

		when(role.getId()).thenReturn("test");
		when(member.getRoles()).thenReturn(Collections.singletonList(role));

		assertFalse(GateKeeper.accessDenied(genericInteractionCreateEvent));
	}

	@Test
	void permissionDeniedIfIntegrationPrivilegeDisabled() {

		permissionGrantedIfIntegrationPrivilegeMatchOnRole();

		when(integrationPrivilege.isEnabled()).thenReturn(false);

		assertTrue(GateKeeper.accessDenied(genericInteractionCreateEvent));
	}

	// Test utility method(s)

	private Command setupMockCommand(String name) {

		when(command.getName()).thenReturn(name);
		when(command.retrievePrivileges(guild)).thenReturn(restActionListIntegrationPrivileges);
		when(restActionListCommands.complete()).thenReturn(Collections.singletonList(command));

		return command;
	}

	private void setupMockIntegrationPrivilege(IntegrationPrivilege.Type type) {

		when(integrationPrivilege.isEnabled()).thenReturn(true);
		when(integrationPrivilege.getType()).thenReturn(type);
		when(integrationPrivilege.getId()).thenReturn("test");

		when(restActionListIntegrationPrivileges.complete()).thenReturn(Collections.singletonList(integrationPrivilege));
	}

}
