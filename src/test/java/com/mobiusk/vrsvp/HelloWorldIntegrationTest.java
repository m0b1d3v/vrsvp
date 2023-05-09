package com.mobiusk.vrsvp;

import io.javalin.http.HttpStatus;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HelloWorldIntegrationTest extends BaseIntegrationTest {

	@Test
	void appReturnsGreetingForRootRoute() {
		JavalinTest.test(javalin, (server, client) -> {

			var response = client.get("/");

			assertEquals(HttpStatus.OK.getCode(), response.code());
			assertNotNull(response.body());
			assertEquals("Testing", response.body().string());
		});
	}
}
