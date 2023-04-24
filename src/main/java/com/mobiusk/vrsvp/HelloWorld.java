package com.mobiusk.vrsvp;

public class HelloWorld {

	public static void main(String[] args) {

		var hello = new HelloWorld();
		var greeting = hello.greeting();

		System.out.println(greeting);
	}

	public String greeting() {
		return "Hello, world!";
	}

}
