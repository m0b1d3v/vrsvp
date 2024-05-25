package dev.m0b1.vrsvp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public final class Json {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
	  .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
	  .enable(SerializationFeature.INDENT_OUTPUT)
	  .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

  public static String write(Object input, String defaultValue) {

    var result = defaultValue;

    try {
      result = OBJECT_MAPPER.writeValueAsString(input);
    } catch (Exception e) {
      log.atError()
        .setMessage("Exception encountered with writing object to JSON")
        .setCause(e)
        .log();
    }

    return result;
  }

}
