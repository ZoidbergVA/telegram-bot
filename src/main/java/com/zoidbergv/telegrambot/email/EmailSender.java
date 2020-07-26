package com.zoidbergv.telegrambot.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailSender.class);

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
			LOGGER.error(e.getLocalizedMessage(), e.getCause());
		}
	}

}
