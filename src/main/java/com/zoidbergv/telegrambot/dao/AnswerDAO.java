package com.zoidbergv.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zoidbergv.telegrambot.model.Answer;

@Repository
public interface AnswerDAO extends JpaRepository<Answer, Integer>{

}
