package com.zoidbergv.telegrambot.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zoidbergv.telegrambot.model.Chat;

@Repository
public interface ChatDAO extends JpaRepository<Chat, Long> {

	List<Chat> findByReported(Boolean reported);
	
}
