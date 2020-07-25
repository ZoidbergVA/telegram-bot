package com.zoidbergv.telegrambot.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zoidbergv.telegrambot.dao.ChatAnswerDAO;
import com.zoidbergv.telegrambot.model.Chat;
import com.zoidbergv.telegrambot.model.ChatAnswer;
import com.zoidbergv.telegrambot.model.Question;
import com.zoidbergv.telegrambot.model.Response;

@Service
@Transactional
public class ChatAnswerService {

	@Autowired
	private ChatAnswerDAO chatAnswerDAO;

	public void add(Chat chat, Question question, Response response) {
		ChatAnswer chatAnswer = new ChatAnswer();
		chatAnswer.setChat(chat);
		chatAnswer.setQuestion(question);
		chatAnswer.setText(response.getText());
		chatAnswer.setWeight(response.getNumber());
		chatAnswer.setDateCreated(LocalDateTime.now());
		chatAnswerDAO.save(chatAnswer);
	}

}