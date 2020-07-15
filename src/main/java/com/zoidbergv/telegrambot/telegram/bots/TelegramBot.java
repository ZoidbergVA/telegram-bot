package com.zoidbergv.telegrambot.telegram.bots;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.zoidbergv.telegrambot.service.AnswerService;
import com.zoidbergv.telegrambot.service.ChatAnswerService;
import com.zoidbergv.telegrambot.service.ChatService;
import com.zoidbergv.telegrambot.service.QuestionService;

@Component
public class TelegramBot extends TelegramLongPollingBot {

	@Value("${telegram.bot.username}")
	private String username;
	@Value("${telegram.bot.token}")
	private String token;

	@Autowired
	private QuestionService questionService;
	@Autowired
	private AnswerService answerService;
	@Autowired
	private ChatAnswerService chatAnswerService;
	@Autowired
	private ChatService chatService;

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			Long chatId = update.getMessage().getChatId();
			if (newChat(update.getMessage().getChatId())) {
				chatService.add(chatId);
				sendMessage(new SendMessage().setChatId(chatId).setText("You're new!"));
			} else {
				sendMessage(new SendMessage().setChatId(update.getMessage().getChatId())
						.setText(update.getMessage().getText()));
			}
		} else {
			sendTryAgain(update.getMessage().getChatId());
		}
	}

	private boolean newChat(Long chatId) {
		return !chatService.getChat(chatId).isPresent();
	}

	private void sendTryAgain(Long chatId) {
		sendMessage(new SendMessage().setChatId(chatId).setText("Try again!"));
	}

	private void sendMessage(SendMessage message) {
		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getBotUsername() {
		return this.username;
	}

	@Override
	public String getBotToken() {
		return this.token;
	}

}
