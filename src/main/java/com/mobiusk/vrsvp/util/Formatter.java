package com.mobiusk.vrsvp.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Formatter {

	public static final String REPLY_PREFIX = "---\n";
	public static final String REPLY_SUFFIX = "\n---";

	public static String replies(String message) {
		return REPLY_PREFIX + message + REPLY_SUFFIX;
	}

	/**
	 * Given a lowercase id string with optional hyphens or context information, format it to a human-readable label.
	 */
	public static String idAsLabel(String id) {

		var label = id.replace("-", " ");

		var contextIndex = label.indexOf(":");
		if (contextIndex != -1) {
			label = label.substring(0, contextIndex);
		}

		var startCharacter = Character.toUpperCase(label.charAt(0));
		return startCharacter + label.substring(1);
	}

}
