package com.zoidbergv.telegrambot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zoidbergv.telegrambot.dao.ChatDAO;
import com.zoidbergv.telegrambot.dao.QuestionDAO;
import com.zoidbergv.telegrambot.model.Chat;

@Service
@Transactional
public class ChatService {

	@Autowired
	private ChatDAO chatDAO;

	@Autowired
	private QuestionDAO questionDAO;

	public Optional<Chat> getChat(Long id) {
		return chatDAO.findById(id);
	}

	public void add(Long chatId) {
		Chat chat = new Chat();
		chat.setId(chatId);
		chat.setCompletedCount(0);
		chatDAO.save(chat);
	}

}
