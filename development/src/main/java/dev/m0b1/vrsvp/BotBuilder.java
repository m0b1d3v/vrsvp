package dev.m0b1.vrsvp;

import dev.m0b1.vrsvp.properties.Properties;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
@RequiredArgsConstructor
public class BotBuilder {

	private final Properties properties;

	@Bean
	public JDA jda() {
		var botToken = properties.getBotSecretToken();
		var intents = EnumSet.noneOf(GatewayIntent.class);
		return JDABuilder.createLight(botToken, intents).build();
	}

}
