package com.zoidbergv.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zoidbergv.telegrambot.model.ChatAnswer;

@Repository
public interface ChatAnswerDAO extends JpaRepository<ChatAnswer, Integer> {

}
