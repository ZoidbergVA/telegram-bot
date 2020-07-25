package com.zoidbergv.telegrambot.model;

import java.util.Set;

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

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "last_answered_question_id", nullable = true)
	private Question lastAnsweredQuestion;

	@OneToMany(mappedBy = "chat", cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<ChatAnswer> chatAnswers;

	@Column(name = "first_name", nullable = true)
	private String firstName;

	@Column(name = "last_name", nullable = true)
	private String lastName;

	@Column(name = "user_name", nullable = true)
	private String userName;
	
	@Column(name = "reported", nullable = false)
	private Boolean reported;
	
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

	public Set<ChatAnswer> getChatAnswers() {
		return chatAnswers;
	}

	public void setChatAnswers(Set<ChatAnswer> chatAnswers) {
		this.chatAnswers = chatAnswers;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getReported() {
		return reported;
	}

	public void setReported(Boolean reported) {
		this.reported = reported;
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
		return "Chat [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", userName=" + userName
				+ ", reported=" + reported + "]";
	}

}
