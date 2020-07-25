package com.zoidbergv.telegrambot.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import com.zoidbergv.telegrambot.email.EmailSender;
import com.zoidbergv.telegrambot.enums.QuestionType;
import com.zoidbergv.telegrambot.model.Chat;
import com.zoidbergv.telegrambot.model.ChatAnswer;
import com.zoidbergv.telegrambot.service.ChatService;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

@Component
public class ReportService {

	@Value("${mail.to-address}")
	private String toAddress;

	@Autowired
	private EmailSender emailSender;

	@Autowired
	private ChatService chatService;

	private String[] toAdresses;

	@PostConstruct
	public void init() {
		this.toAdresses = this.toAddress.split(";");
	}

	public void sendReport() {
		String dateTime = LocalDateTime.now().toString();
		String fileName = "Telegram bot report " + dateTime.substring(0, dateTime.lastIndexOf(".")) + ".xls";
		List<Chat> chats = chatService.updateReported(chatService.getUnreportedChats());
		if (chats.size() > 0) {
			emailSender.sendMessageWithAttachment(toAdresses, "Report", "", fileName,
					new ByteArrayResource(generateXls(chats).toByteArray()), "application/vnd.ms-excel");
		}
	}

	private ByteArrayOutputStream generateXls(List<Chat> chats) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(outputStream);

			WritableSheet sheet = workbook.createSheet("Sheet 1", 0);

			sheet.setColumnView(0, 20);
			sheet.addCell(new Label(0, 0, "Name"));
			int column = 1;
			for (QuestionType questionType : QuestionType.values()) {
				sheet.addCell(new Label(column, 0, questionType.name()));
				sheet.setColumnView(column++, 15);
				sheet.addCell(new Label(column, 0, questionType.name() + "%"));
				sheet.setColumnView(column++, 15);
			}

			int row = 1;
			for (Chat chat : chats) {
				column = 0;
				String name = chat.getFirstName() + (chat.getLastName() != null ? " " + chat.getLastName() : "")
						+ (chat.getUserName() != null ? " : " + chat.getUserName() : "");
				sheet.addCell(new Label(column, row, name));

				Map<String, Integer> resultsMap = new HashMap<>();
				final String TOTAL = "total";
				resultsMap.put(TOTAL, 0);
				for (QuestionType questionType : QuestionType.values()) {
					resultsMap.put(questionType.name(), 0);
				}

				for (ChatAnswer chatAnswer : chat.getChatAnswers()) {
					resultsMap.put(TOTAL, resultsMap.get(TOTAL) + chatAnswer.getWeight());
					resultsMap.put(chatAnswer.getQuestion().getType().name(),
							resultsMap.get(chatAnswer.getQuestion().getType().name()) + chatAnswer.getWeight());
				}

				for (QuestionType questionType : QuestionType.values()) {
					column++;
					sheet.addCell(new Label(column, row, resultsMap.get(questionType.name()).toString()));
					column++;
					Double percentage = Double.valueOf(resultsMap.get(questionType.name())) / resultsMap.get(TOTAL)
							* 100;
					sheet.addCell(new Label(column, row, percentage.toString()));
				}
				row++;
			}

			workbook.write();
			workbook.close();
		} catch (IOException | WriteException e) {
			e.printStackTrace();
		}
		return outputStream;
	}

//	public ByteArrayOutputStream generateOds(List<Chat> chats) {
//		try {
//			int rows = chats.size() + 1;
//			int columns = 7;
//			Sheet sheet = new Sheet("Report", rows, columns);
//
//			sheet.getRange(0, 0, 1, 7).setValues("Name", QuestionType.AGGRESSIVE.name(),
//					QuestionType.AGGRESSIVE.name() + "%", QuestionType.ASSERTIVE.name(),
//					QuestionType.ASSERTIVE.name() + "%", QuestionType.PASSIVE.name(),
//					QuestionType.PASSIVE.name() + "%");
//
//			SpreadSheet spreadSheet = new SpreadSheet();
//			spreadSheet.appendSheet(sheet);
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			spreadSheet.save(outputStream);
//			return outputStream;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

}
