package dev.m0b1.vrsvp.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(value = "application.discord")
@Data
public class Properties {

	@NotEmpty
	private String botSecretToken;

	private PropertiesWebhook webhook;

}
