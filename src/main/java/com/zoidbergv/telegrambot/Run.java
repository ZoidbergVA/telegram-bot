package com.zoidbergv.telegrambot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.zoidbergv.telegrambot.telegram.bots.TelegramBot;

@Component
public class Run {

	@Autowired
	private TelegramBot telegramBot;

	@EventListener(ApplicationReadyEvent.class)
	public void runBot() {
		TelegramBotsApi botsApi = new TelegramBotsApi();
		try {
			botsApi.registerBot(telegramBot);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
