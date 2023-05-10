package com.mobiusk.vrsvp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

class HttpServerUnitTest extends TestBase {

	@Test
	void defaultJavalinAppCreated() {
		httpServer.create();
		assertNotNull(javalin);
	}

	@Test
	void javalinAppDoesNotLogBannerAfterConfiguration() {
		verify(javalin).updateConfig(any());
		assertFalse(javalin.cfg.showJavalinBanner);
	}

	@Test
	void javalinAppStarted() {
		doReturn(javalin).when(javalin).start();
		httpServer.start();
		verify(javalin).start();
	}

}
