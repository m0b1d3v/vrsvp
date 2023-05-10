package com.mobiusk.vrsvp;

import io.javalin.Javalin;

public class HttpServer {

	private Javalin javalin;

	public void create() {
		javalin = Javalin.create();
	}

	public void configure(String greeting) {
		javalin
			.updateConfig(config -> config.showJavalinBanner = false)
			.get("/", context -> context.result(greeting));
	}

	public void start() {
		javalin.start();
	}

}
