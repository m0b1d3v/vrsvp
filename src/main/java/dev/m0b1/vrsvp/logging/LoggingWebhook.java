package dev.m0b1.vrsvp.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import dev.m0b1.vrsvp.util.Json;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class LoggingWebhook extends AppenderBase<ILoggingEvent> {

	private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

	private String destination;

	@Override
	protected void append(ILoggingEvent iLoggingEvent) {
		if (destination != null && destination.startsWith("http")) {
			try {

				var content = formatContent(iLoggingEvent);
				var body = formatBody(content);
				var request = buildHttpRequest(body);

				HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());

			} catch (Exception e) {
				addWarn("Could not send Discord message", e);
			}
		}
	}

	private String formatContent(ILoggingEvent event) {

		var content = new HashMap<String, Object>();

		addKvp(content, event);
		addLevel(content, event);
		addLogger(content, event);
		addMdc(content, event);
		addMessage(content, event);
		addThrowable(content, event);
		addTimestamp(content, event);

		var result = Json.write(content, "Log content data not recognized");
		return STR."```json\n\{result}\n```";
	}

	private void addKvp(Map<String, Object> map, ILoggingEvent event) {
		var value = event.getKeyValuePairs();
		if (value != null) {
			map.put("kvp", value);
		}
	}

	private void addLevel(Map<String, Object> map, ILoggingEvent event) {
		var value = event.getLevel();
		if (value != null) {
			map.put("level", value.levelStr);
		}
	}

	private void addLogger(Map<String, Object> map, ILoggingEvent event) {
		map.put("logger", event.getLoggerName());
	}

	private void addMdc(Map<String, Object> map, ILoggingEvent event) {
		var value = event.getMDCPropertyMap();
		if (value != null && ! value.isEmpty()) {
			map.put("mdc", value);
		}
	}

	private void addMessage(Map<String, Object> map, ILoggingEvent event) {
		map.put("message", event.getFormattedMessage());
	}

	private void addThrowable(Map<String, Object> map, ILoggingEvent event) {
		var value = event.getThrowableProxy();
		if (value != null) {
			var exception = ThrowableProxyUtil.asString(value);
			exception = StringUtils.truncate(exception, 1000);
			var exceptionArray = exception.split("\r\n\t");
			map.put("exception", exceptionArray);
		}
	}

	private void addTimestamp(Map<String, Object> map, ILoggingEvent event) {
		var value = event.getInstant();
		if (value != null) {
			map.put("timestamp", value.toString());
		}
	}

	private String formatBody(String content) {

		var source = new HashMap<String, Object>();
		source.put("content", content);

		return Json.write(source, "Log body data not recognized");
	}

	private HttpRequest buildHttpRequest(String body) throws URISyntaxException {

		var uri = new URI(destination);

		return HttpRequest.newBuilder()
			.uri(uri)
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.timeout(Duration.of(10, ChronoUnit.SECONDS))
			.build();
	}
}
