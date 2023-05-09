package com.mobiusk.vrsvp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class HelloWorldUnitTest {

    @Test void appHasAGreeting() {
		var hello = new HelloWorld();
		assertNotNull(hello.greeting());
    }
}
