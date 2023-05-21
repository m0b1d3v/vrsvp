package com.mobiusk.vrsvp.util;

import com.mobiusk.vrsvp.TestBase;
import net.dv8tion.jda.api.Permission;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class GateKeeperUnitTest extends TestBase {

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
		when(genericInteractionCreateEvent.getMember()).thenReturn(member);
		assertFalse(GateKeeper.accessDenied(genericInteractionCreateEvent));
	}

	@Test
	void permissionDeniedIfEventMemberExistsAndIsNotAdministrator() {
		when(member.hasPermission(Permission.ADMINISTRATOR)).thenReturn(false);
		when(genericInteractionCreateEvent.getMember()).thenReturn(member);
		assertTrue(GateKeeper.accessDenied(genericInteractionCreateEvent));
	}

}
