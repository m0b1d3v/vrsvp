package dev.m0b1.vrsvp.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(value = "application.discord")
@Data
@Validated
public class Properties {

	public static final String FORM_NOT_FOUND_REPLY = "Cannot find event post. Was it deleted? Does the bot have read permissions?";

	@NotBlank
	private String botSecretToken;

	private String logWebhook;

}
