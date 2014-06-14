package com.zetterstrom.android.soundboarder.Util;

public class StringParser {
	public static String splitCamelCase(String fileName) {
		String split = fileName;
		split = split.substring(0, split.lastIndexOf('.'));
		return split.replaceAll(String.format("%s|%s|%s",
				"(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
	}
}
