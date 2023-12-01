package dev.m0b1.vrsvp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public final class Json {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
