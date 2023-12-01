package dev.m0b1.vrsvp.logging;

import dev.m0b1.vrsvp.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class ServiceDiscord {

	private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
	private static final String DISCORD_WEBHOOK_AVATAR = System.getenv("VRSVP_DISCORD_WEBHOOK_AVATAR");
	private static final String DISCORD_WEBHOOK_DESTINATION = System.getenv("VRSVP_DISCORD_WEBHOOK_DESTINATION");

	public void run(Map<String, Object> data) {

		if (DISCORD_WEBHOOK_AVATAR != null && ! DISCORD_WEBHOOK_AVATAR.isBlank()
			&& DISCORD_WEBHOOK_DESTINATION != null && ! DISCORD_WEBHOOK_DESTINATION.isBlank()
		) {
			try {

				var content = formatContent(data);
				var body = formatBody(content);
				var request = buildHttpRequest(body);

				HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());

			} catch (Exception e) {
				log.atError().setMessage("Could not send Discord message").setCause(e).log();
			}
		}
	}

	private String formatContent(Map<String, Object> data) {
		var result = Json.write(data, "Discord data not recognized");
		return String.format("```json\n%s\n```", result);
	}

	private String formatBody(String content) {

		var source = new LinkedHashMap<String, Object>();
		source.put("content", content);
		source.put("username", "VRSVP");
		source.put("avatar_url", DISCORD_WEBHOOK_AVATAR);

		return Json.write(source, "Log data not recognized");
	}

	private HttpRequest buildHttpRequest(String body) throws URISyntaxException {

		var uri = new URI(DISCORD_WEBHOOK_DESTINATION);

		return HttpRequest.newBuilder()
			.uri(uri)
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.timeout(Duration.of(10, ChronoUnit.SECONDS))
			.build();
	}

}
