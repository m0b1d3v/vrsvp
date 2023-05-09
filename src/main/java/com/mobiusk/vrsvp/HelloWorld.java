package com.mobiusk.vrsvp;

import io.javalin.Javalin;

public class HelloWorld {

	public static void main(String[] args) {
		var javalin = Javalin.create();
		configure(javalin, "Hello, world");
		javalin.start();
	}

	public static void configure(Javalin javalin, String greeting) {
		javalin
			.get("/", context -> context.result(greeting));
	}

}
