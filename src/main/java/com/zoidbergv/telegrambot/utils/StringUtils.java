package com.zoidbergv.telegrambot.utils;

public class StringUtils {

	public static String insertUser(String message, String placeHolder, String firstName, String lastName) {
		return message.replace(placeHolder, lastName != null ? firstName + " " + lastName : firstName);
	}

}
