package com.zoidbergv.telegrambot.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	@Autowired
	private JavaMailSender emailSender;

	public void sendMessageWithAttachment(String[] toAdresses, String subject, String text, String fileName,
			InputStreamSource inputStreamSource, String mimeType) {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(toAdresses);
			helper.setSubject(subject);
			helper.setText(text);
			helper.addAttachment(fileName, inputStreamSource, mimeType);

			emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
