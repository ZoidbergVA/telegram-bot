package com.zoidbergv.telegrambot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zoidbergv.telegrambot.dao.ChatDAO;
import com.zoidbergv.telegrambot.model.Chat;
import com.zoidbergv.telegrambot.model.Question;

@Service
@Transactional
public class ChatService {

	@Autowired
	private ChatDAO chatDAO;

	public Optional<Chat> getChat(Long id) {
		return chatDAO.findById(id);
	}

	public Chat add(Long chatId, String firstName, String lastName, String userName) {
		Chat chat = new Chat();
		chat.setId(chatId);
		chat.setFirstName(firstName);
		chat.setLastName(lastName);
		chat.setUserName(userName);
		chat.setReported(false);
		return chatDAO.save(chat);
	}

	public Chat updateLastAnswered(Chat chat, Question question) {
		chat.setLastAnsweredQuestion(question);
		return chatDAO.save(chat);
	}

	public List<Chat> updateReported(List<Chat> chats) {
		chats.stream().forEach(chat -> chat.setReported(true));
		return chatDAO.saveAll(chats);
	}

	public Chat resetChat(Chat chat) {
		chat.setLastAnsweredQuestion(null);
		chat.setReported(false);
		return chatDAO.save(chat);
	}

	public List<Chat> getUnreportedChats() {
		return chatDAO.findByReported(false);
	}

}
