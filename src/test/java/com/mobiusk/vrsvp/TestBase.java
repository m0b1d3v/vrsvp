package com.mobiusk.vrsvp;

import io.javalin.Javalin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class TestBase {

	@InjectMocks
	protected HttpServer httpServer;

	@Spy
	protected Javalin javalin;

	@BeforeEach
	void beforeEach() {
		httpServer.configure("Testing");
	}

}
