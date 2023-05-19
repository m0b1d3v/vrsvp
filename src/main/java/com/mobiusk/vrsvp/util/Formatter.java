package com.mobiusk.vrsvp.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Formatter {

	public static final String REPLY_PREFIX = "---\n";
	public static final String REPLY_SUFFIX = "\n---";

	public static String replies(String message) {
		return REPLY_PREFIX + message + REPLY_SUFFIX;
	}

}
