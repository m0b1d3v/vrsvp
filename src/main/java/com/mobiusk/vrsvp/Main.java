package com.mobiusk.vrsvp;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		startDiscordBot();
		startServer();
	}

	private static void startDiscordBot() throws InterruptedException {
		var discordBot = new DiscordBot();
		discordBot.create();
		discordBot.configure();
		discordBot.start();
	}

	private static void startServer() {
		var httpServer = new HttpServer();
		httpServer.create();
		httpServer.configure("Hello, world");
		httpServer.start();
	}

}
