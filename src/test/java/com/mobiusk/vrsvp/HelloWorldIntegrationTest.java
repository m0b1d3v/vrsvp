package com.mobiusk.vrsvp;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HelloWorldIntegrationTest extends BaseIntegrationTest {

	private static HelloWorld helloWorld;

	private static Javalin javalin;

	@BeforeAll
	static void beforeAll() {

		helloWorld = new HelloWorld();

		var greeting = helloWorld.greeting();
		javalin = helloWorld.createApp(greeting);
	}

    @Test void appReturnsGreetingForRootRoute() {
		JavalinTest.test(javalin, (server, client) -> {

			var response = client.get("/");

			assertEquals(HttpStatus.OK.getCode(), response.code());
			assertNotNull(response.body());
			assertEquals(helloWorld.greeting(), response.body().string());
		});
    }
}
