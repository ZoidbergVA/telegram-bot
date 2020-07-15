package com.zoidbergv.telegrambot.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "chat")
public class Chat {

	@Id
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "last_answered_question_id", nullable = true)
	private Question lastAnsweredQuestion;
	
	@Column(name = "completed_count", nullable = true)
	private Integer completedCount;

	@OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<ChatAnswer> chatAnswers;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Question getLastAnsweredQuestion() {
		return lastAnsweredQuestion;
	}

	public void setLastAnsweredQuestion(Question lastAnsweredQuestion) {
		this.lastAnsweredQuestion = lastAnsweredQuestion;
	}

	public Integer getCompletedCount() {
		return completedCount;
	}

	public void setCompletedCount(Integer completedCount) {
		this.completedCount = completedCount;
	}

	public Set<ChatAnswer> getChatAnswers() {
		return chatAnswers;
	}

	public void setChatAnswers(Set<ChatAnswer> chatAnswers) {
		this.chatAnswers = chatAnswers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Chat other = (Chat) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Chat [id=" + id + ", completedCount=" + completedCount + "]";
	}
	
}
