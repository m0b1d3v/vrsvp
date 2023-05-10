package com.mobiusk.vrsvp;

public class Main {

	public static void main(String[] args) {
		startServer();
	}


	private static void startServer() {
		var httpServer = new HttpServer();
		httpServer.create();
		httpServer.configure("Hello, world");
		httpServer.start();
	}

}
