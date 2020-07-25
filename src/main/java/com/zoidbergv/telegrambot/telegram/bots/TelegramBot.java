package com.zoidbergv.telegrambot.telegram.bots;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.zoidbergv.telegrambot.model.Chat;
import com.zoidbergv.telegrambot.model.Question;
import com.zoidbergv.telegrambot.model.Response;
import com.zoidbergv.telegrambot.service.ChatAnswerService;
import com.zoidbergv.telegrambot.service.ChatService;
import com.zoidbergv.telegrambot.service.QuestionService;
import com.zoidbergv.telegrambot.service.ResponseService;
import com.zoidbergv.telegrambot.utils.StringUtils;

@Component
public class TelegramBot extends TelegramLongPollingBot {

	private final static String PLACE_HOLDER = "${userName}";
	private final static String GREETING_MESSAGE = "Ciao " + PLACE_HOLDER + ",\n"
			+ "Benvenuto nel questionario automatizzato.\n"
			+ "La durata del completamento del questionario è di un massimo di 10 minuti.\n"
			+ "Il numero di domande nel questionario è di 36.";
	private final static String ERROR_MESSAGE = "Oops... la risposta non è adeguata.\n"
			+ "Si prega di utilizzare una delle 4 opzioni di risposta.";
	private final static String DONE_MESSAGE = "Hai completato con successo il questionario. \n" + "Grazie "
			+ PLACE_HOLDER + " per il tuo tempo. \n";
	private final static String ALREADY_FINISH_MESSAGE = PLACE_HOLDER + ", hai già completato il questionario. \n"
			+ "Grazie. \n";

	private final static String RESET = "/reset";

	@Value("${telegram.bot.username}")
	private String username;
	@Value("${telegram.bot.token}")
	private String token;

	private ReplyKeyboardMarkup replyKeyboardMarkup;

	@Autowired
	private QuestionService questionService;
	@Autowired
	private ChatAnswerService chatAnswerService;
	@Autowired
	private ChatService chatService;
	@Autowired
	private ResponseService responseService;

	@PostConstruct
	public void init() {
		List<Response> responses = responseService.getResponses();
		List<KeyboardRow> rows = new ArrayList<>();
		for (Response response : responses) {
			KeyboardButton keyboardButton = new KeyboardButton();
			keyboardButton.setText(response.getText());
			KeyboardRow keyboardRow = new KeyboardRow();
			keyboardRow.add(keyboardButton);
			rows.add(keyboardRow);
		}
		this.replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			handleMessage(update);
		} else {
			sendTryAgain(update.getMessage().getChatId());
		}
	}

	private void handleMessage(Update update) {
		Long chatId = update.getMessage().getChatId();
		Optional<Chat> chat = chatService.getChat(chatId);
		Optional<Question> question = Optional.ofNullable(null);

		/*
		 * new chat
		 */
		if (!chat.isPresent()) {
			chat = Optional.of(chatService.add(chatId, update.getMessage().getFrom().getFirstName(),
					update.getMessage().getFrom().getLastName(), update.getMessage().getFrom().getUserName()));
			sendMessage(new SendMessage().setChatId(chatId).setText(StringUtils.insertUser(GREETING_MESSAGE,
					PLACE_HOLDER, chat.get().getFirstName(), chat.get().getLastName())));
			question = questionService.getQuestionByChat(chat.get());
			sendQuestion(chat.get(), question);
			return;
		}

		/*
		 * reset
		 */
		if (RESET.equals(update.getMessage().getText())) {
			chat = Optional.of(chatService.resetChat(chat.get()));
			question = questionService.getQuestionByChat(chat.get());
			sendQuestion(chat.get(), question);
			return;
		}

		/*
		 * finished chat
		 */
		question = questionService.getQuestionByChat(chat.get());
		if (!question.isPresent() && chat.get().getLastAnsweredQuestion() != null) {
			sendMessage(new SendMessage().setChatId(chatId).setText(StringUtils.insertUser(ALREADY_FINISH_MESSAGE,
					PLACE_HOLDER, chat.get().getFirstName(), chat.get().getLastName())));
			return;
		}

		/*
		 * answer to existing chat
		 */
		Optional<Response> response = responseService.getReponseByText(update.getMessage().getText());
		if (response.isPresent()) {
			question = questionService.getQuestionByChat(chat.get());
			chatAnswerService.add(chat.get(), question.get(), response.get());

			chat = Optional.of(chatService.updateLastAnswered(chat.get(), question.get()));
			question = questionService.getQuestionByChat(chat.get());
			sendQuestion(chat.get(), question);
		} else {
			sendTryAgain(chatId);
		}
	}

	private void sendQuestion(Chat chat, Optional<Question> question) {
		if (question.isPresent()) {
			sendMessage(new SendMessage().setChatId(chat.getId()).setText(question.get().getText())
					.setReplyMarkup(replyKeyboardMarkup));
		} else {
			sendMessage(new SendMessage().setChatId(chat.getId()).setText(
					StringUtils.insertUser(DONE_MESSAGE, PLACE_HOLDER, chat.getFirstName(), chat.getLastName())));
		}
	}

	private void sendTryAgain(Long chatId) {
		Optional<Chat> chat = chatService.getChat(chatId);
		Optional<Question> question = questionService.getQuestionByChat(chat.get());
		sendMessage(new SendMessage().setChatId(chatId).setText(ERROR_MESSAGE));
		sendQuestion(chat.get(), question);
	}

	private void sendMessage(SendMessage message) {
		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getBotUsername() {
		return this.username;
	}

	@Override
	public String getBotToken() {
		return this.token;
	}

}
