package com.mobiusk.vrsvp;

import io.javalin.Javalin;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseIntegrationTest extends BaseUnitTest {

	protected static Javalin javalin;

	@BeforeAll
	static void beforeAll() {
		javalin = Javalin.create();
		HelloWorld.configure(javalin, "Testing");
	}

}
