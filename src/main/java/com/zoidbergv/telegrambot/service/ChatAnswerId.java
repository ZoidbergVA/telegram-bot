package com.zoidbergv.telegrambot.service;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ChatAnswerId implements Serializable {

	private static final long serialVersionUID = 9094046334528188850L;

	@Column(name = "chat_id")
	private Long chatId;

	@Column(name = "question_id")
	private Integer questionId;

	public ChatAnswerId() {
		super();
	}

	public ChatAnswerId(Long chatId, Integer questionId) {
		super();
		this.chatId = chatId;
		this.questionId = questionId;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chatId == null) ? 0 : chatId.hashCode());
		result = prime * result + ((questionId == null) ? 0 : questionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatAnswerId other = (ChatAnswerId) obj;
		if (chatId == null) {
			if (other.chatId != null)
				return false;
		} else if (!chatId.equals(other.chatId))
			return false;
		if (questionId == null) {
			if (other.questionId != null)
				return false;
		} else if (!questionId.equals(other.questionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChatAnswerId [chatId=" + chatId + ", questionId=" + questionId + "]";
	}

}
