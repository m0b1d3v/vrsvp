package com.mobiusk.vrsvp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HelloWorldTest {

    @Test void appHasAGreeting() {
		var hello = new HelloWorld();
		assertNotNull(hello.greeting());
    }
}
