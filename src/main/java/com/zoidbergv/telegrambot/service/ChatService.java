package com.zoidbergv.telegrambot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zoidbergv.telegrambot.dao.ChatDAO;
import com.zoidbergv.telegrambot.dao.QuestionDAO;
import com.zoidbergv.telegrambot.enums.ChatState;
import com.zoidbergv.telegrambot.model.Chat;
import com.zoidbergv.telegrambot.model.Question;

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

	public Chat add(Long chatId, String firstName, String lastName, String userName) {
		Chat chat = new Chat();
		chat.setId(chatId);
		chat.setFirstName(firstName);
		chat.setLastName(lastName);
		chat.setUserName(userName);
		chat.setState(ChatState.ANSWERING);
		return chatDAO.save(chat);
	}

	public Chat updateLastAnswered(Chat chat, Question question) {
		chat.setLastAnsweredQuestion(question);
		if (questionDAO.findFirstByOrderByIdDesc().get().equals(question)) {
			chat.setState(ChatState.RETRY_SENT);
		}
		return chatDAO.save(chat);
	}

	public Chat updateState(Chat chat, ChatState state) {
		chat.setState(state);
		return chatDAO.save(chat);
	}

	public List<Chat> updateReported(List<Chat> chats) {
		chats.stream().forEach(chat -> chat.setState(ChatState.REPORTED));
		return chatDAO.saveAll(chats);
	}

	public Chat resetChat(Chat chat) {
		chat.setLastAnsweredQuestion(null);
		chat.setState(ChatState.ANSWERING);
		return chatDAO.save(chat);
	}

	public Chat retryLastQuestion(Chat chat) {
		Optional<Question> question = questionDAO.findById(chat.getLastAnsweredQuestion() != null
				? (chat.getLastAnsweredQuestion().getId() != 1 ? chat.getLastAnsweredQuestion().getId() - 1 : 0)
				: 0);
		chat.setLastAnsweredQuestion(question.isPresent() ? question.get() : null);
		return chatDAO.save(chat);

	}

	public List<Chat> getUnreportedChats() {
		return chatDAO.findByState(ChatState.FINISHED);
	}

}
