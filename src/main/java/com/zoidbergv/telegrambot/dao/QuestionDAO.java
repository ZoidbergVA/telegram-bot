package com.zoidbergv.telegrambot.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zoidbergv.telegrambot.model.Question;

@Repository
public interface QuestionDAO extends JpaRepository<Question, Integer> {

	public Optional<Question> findFirstByOrderByIdDesc();

	public Optional<Question> findFirstByText(String text);

}
