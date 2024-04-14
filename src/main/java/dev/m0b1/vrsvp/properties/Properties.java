package dev.m0b1.vrsvp.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(value = "application.discord")
@Data
public class Properties {

	public static final String FORM_NOT_FOUND_REPLY = "Cannot find event post. Was it deleted? Does the bot have read permissions?";

	@NotEmpty
	private String botSecretToken;

	private String logWebhook;

}
