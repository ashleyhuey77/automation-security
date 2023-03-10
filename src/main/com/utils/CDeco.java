package com.utils;

public enum CDeco {

	RED_TEXT("\u001B[31m"),
	BLACK_TEXT("\u001B[30m"),
	GREEN_TEXT("\033[92m"),
	CYAN_TEXT("\u001B[36m"),
	WHITE_TEXT("\033[97m"),
	RESET("\u001B[0m"),
	BOLD_TEXT("\u001b[1m"),
	BLACK_BACKGROUND("\u001b[100;2m"),
	RED_BACKGROUND("\033[101;1m"),
	GREEN_BACKGROUND("\u001b[102;1m"),
	CYAN_BACKGROUND("\u001b[46;1m"),
	WHITE_BACKGROUND("\u001b[47;1m");

	public final String value;

	CDeco(String value) {
		this.value = value;
	}
}
