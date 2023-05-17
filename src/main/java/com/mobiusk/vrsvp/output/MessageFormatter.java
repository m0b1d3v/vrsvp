package com.mobiusk.vrsvp.output;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageFormatter {

	public static final String REPLY_PREFIX = "---\n";
	public static final String REPLY_SUFFIX = "\n---";

	public static String output(String message) {
		return REPLY_PREFIX + message + REPLY_SUFFIX;
	}

}
