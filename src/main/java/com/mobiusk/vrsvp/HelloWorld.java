package com.mobiusk.vrsvp;

import io.javalin.Javalin;

public class HelloWorld {

	public static void main(String[] args) {
		var hello = new HelloWorld();
		var greeting = hello.greeting();
		hello.createApp(greeting).start();
	}

	public Javalin createApp(String greeting) {
		return Javalin.create()
			.get("/", context -> context.result(greeting));
	}

	public String greeting() {
		return "Hello, world!";
	}

}
