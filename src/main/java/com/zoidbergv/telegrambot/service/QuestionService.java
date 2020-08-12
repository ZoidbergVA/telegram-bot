package com.zoidbergv.telegrambot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zoidbergv.telegrambot.dao.QuestionDAO;
import com.zoidbergv.telegrambot.model.Chat;
import com.zoidbergv.telegrambot.model.Question;

@Service
@Transactional
public class QuestionService {

	@Autowired
	private QuestionDAO questionDAO;

	public Optional<Question> getQuestionByChat(Chat chat) {
		Optional<Question> question = Optional.ofNullable(null);
		Integer questionId = chat.getLastAnsweredQuestion() != null ? chat.getLastAnsweredQuestion().getId() : 0;
		question = questionDAO.findById(questionId + 1);
		return question;
	}

	public List<Question> getQuestions() {
		return questionDAO.findAll();
	}

	public Optional<Question> getQuestionByText(String text) {
		return questionDAO.findFirstByText(text);
	}

}
