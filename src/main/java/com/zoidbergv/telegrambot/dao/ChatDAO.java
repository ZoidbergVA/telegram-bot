package com.zoidbergv.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zoidbergv.telegrambot.model.Chat;

@Repository
public interface ChatDAO extends JpaRepository<Chat, Long> {

}