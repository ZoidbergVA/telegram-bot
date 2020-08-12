package com.zoidbergv.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zoidbergv.telegrambot.model.Chat;
import com.zoidbergv.telegrambot.model.ChatAnswer;
import com.zoidbergv.telegrambot.model.Question;

@Repository
public interface ChatAnswerDAO extends JpaRepository<ChatAnswer, Integer> {

	public ChatAnswer findByChatAndQuestion(Chat chat, Question question);

}
