package com.mobiusk.vrsvp;

import io.javalin.Javalin;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class HelloWorldUnitTest extends BaseUnitTest {

	@Spy
	private Javalin javalin;

	@Test
	void javalinAppDoesNotLogBanner() {

		HelloWorld.configure(javalin, null);

		verify(javalin).updateConfig(any());

		assertFalse(javalin.cfg.showJavalinBanner);
	}

}
