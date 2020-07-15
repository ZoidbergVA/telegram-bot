package com.zoidbergv.telegrambot.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.zoidbergv.telegrambot.enums.QuestionType;

@Entity
@Table(name = "question")
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "text", nullable = false)
	private String text;

	@Column(name = "type", nullable = false, columnDefinition = "smallint")
	private QuestionType questionType;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<Answer> answers;
	
	@OneToMany(mappedBy = "nextQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<Answer> possiblePreviousAnswers;

	@OneToMany(mappedBy = "lastAnsweredQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<Chat> currentChats;
	
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<ChatAnswer> chatAnswers;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public Set<Answer> getPossiblePreviousAnswers() {
		return possiblePreviousAnswers;
	}

	public void setPossiblePreviousAnswers(Set<Answer> possiblePreviousAnswers) {
		this.possiblePreviousAnswers = possiblePreviousAnswers;
	}

	public Set<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(Set<Answer> answers) {
		this.answers = answers;
	}

	public Set<Chat> getCurrentChats() {
		return currentChats;
	}

	public void setCurrentChats(Set<Chat> currentChats) {
		this.currentChats = currentChats;
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
		Question other = (Question) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", text=" + text + ", questionType=" + questionType + "]";
	}

}
