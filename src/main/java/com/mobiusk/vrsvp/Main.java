package com.mobiusk.vrsvp;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		var discordBot = new DiscordBot();
		discordBot.create();
		discordBot.configure();
		discordBot.start();
	}

}
