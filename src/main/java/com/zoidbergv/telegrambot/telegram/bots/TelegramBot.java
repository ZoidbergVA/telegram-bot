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

import com.zoidbergv.telegrambot.enums.ChatState;
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
			+ "Si prega di utilizzare una delle opzioni di risposta.";
	private final static String DONE_MESSAGE = "Hai completato con successo il questionario. \n" + "Grazie "
			+ PLACE_HOLDER + " per il tuo tempo. \n";
	private final static String ALREADY_FINISH_MESSAGE = PLACE_HOLDER + ", hai già completato il questionario. \n"
			+ "Grazie. \n";

	private final static String RETRY_LAST_QUESTION_ANSWER = "retry last question";

	private final static String RETRY_ANY_QUESTION = "do you want to retry any questions?";
	private final static String RETRY_ANY_QUESTIONS_YES = "yes";
	private final static String RETRY_ANY_QUESTIONS_NO = "no";
	private final static String RETRY_ANY_QUESTIONS_CHOSE = "please chose your question";

	private final static String RESET = "/reset";

	@Value("${telegram.bot.username}")
	private String username;
	@Value("${telegram.bot.token}")
	private String token;

	private ReplyKeyboardMarkup answersKeyboardMarkup;
	private ReplyKeyboardMarkup questionsKeyboardMarkup;
	private ReplyKeyboardMarkup retryKeyboardMarkup;

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
		responses.add(new Response(null, RETRY_LAST_QUESTION_ANSWER));
		List<KeyboardRow> answersKeyboardRows = new ArrayList<>();
		for (Response response : responses) {
			KeyboardButton keyboardButton = new KeyboardButton();
			keyboardButton.setText(response.getText());
			KeyboardRow keyboardRow = new KeyboardRow();
			keyboardRow.add(keyboardButton);
			answersKeyboardRows.add(keyboardRow);
		}
		this.answersKeyboardMarkup = new ReplyKeyboardMarkup(answersKeyboardRows);

		List<Question> questions = questionService.getQuestions();
		List<KeyboardRow> questionsKeyboardRows = new ArrayList<>();
		for (Question queston : questions) {
			KeyboardButton keyboardButton = new KeyboardButton();
			keyboardButton.setText(queston.getText());
			KeyboardRow keyboardRow = new KeyboardRow();
			keyboardRow.add(keyboardButton);
			questionsKeyboardRows.add(keyboardRow);
		}
		this.questionsKeyboardMarkup = new ReplyKeyboardMarkup(questionsKeyboardRows);

		List<KeyboardRow> retryKeyboardRows = new ArrayList<>();
		KeyboardRow yesKeyboardRow = new KeyboardRow();
		KeyboardButton yesButton = new KeyboardButton();
		yesButton.setText(RETRY_ANY_QUESTIONS_YES);
		yesKeyboardRow.add(yesButton);
		KeyboardRow noKeyboardRow = new KeyboardRow();
		KeyboardButton noButton = new KeyboardButton();
		noButton.setText(RETRY_ANY_QUESTIONS_NO);
		noKeyboardRow.add(noButton);
		retryKeyboardRows.add(yesKeyboardRow);
		retryKeyboardRows.add(noKeyboardRow);
		this.retryKeyboardMarkup = new ReplyKeyboardMarkup(retryKeyboardRows);
		this.retryKeyboardMarkup.setOneTimeKeyboard(true);
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
			sendQuestion(chat.get(), question, this.answersKeyboardMarkup);
			return;
		}

		/*
		 * reset
		 */
		if (RESET.equals(update.getMessage().getText())) {
			chat = Optional.of(chatService.resetChat(chat.get()));
			question = questionService.getQuestionByChat(chat.get());
			sendQuestion(chat.get(), question, this.answersKeyboardMarkup);
			return;
		}

		/*
		 * ask if retry
		 */
		if (chat.get().getState() == ChatState.RETRY_SENT) {
			if (RETRY_ANY_QUESTIONS_YES.equals(update.getMessage().getText())) {
				sendMessage(new SendMessage().setChatId(chatId).setText(RETRY_ANY_QUESTIONS_CHOSE)
						.setReplyMarkup(questionsKeyboardMarkup));
				chat = Optional.of(chatService.updateState(chat.get(), ChatState.RETRY_YES));
			} else if (RETRY_ANY_QUESTIONS_NO.equals(update.getMessage().getText())) {
				sendMessage(new SendMessage().setChatId(chat.get().getId()).setText(StringUtils.insertUser(DONE_MESSAGE,
						PLACE_HOLDER, chat.get().getFirstName(), chat.get().getLastName())));
				chat = Optional.of(chatService.updateState(chat.get(), ChatState.FINISHED));
			} else {
				sendTryAgain(chatId);
			}
			return;
		}

		/*
		 * get retry question
		 */
		if (chat.get().getState() == ChatState.RETRY_YES) {
			question = questionService.getQuestionByText(update.getMessage().getText());
			if (question.isPresent()) {
				chat = Optional.of(chatService.updateLastAnswered(chat.get(), question.get()));
				chat = Optional.of(chatService.retryLastQuestion(chat.get()));
				chat = Optional.of(chatService.updateState(chat.get(), ChatState.RETRY_QUESTION));
				question = questionService.getQuestionByChat(chat.get());
				sendQuestion(chat.get(), question, answersKeyboardMarkup);
			} else {
				sendTryAgain(chatId);
			}
			return;
		}

		/*
		 * finished chat
		 */
		if (chat.get().getState() == ChatState.FINISHED || chat.get().getState() == ChatState.REPORTED) {
			sendMessage(new SendMessage().setChatId(chatId).setText(StringUtils.insertUser(ALREADY_FINISH_MESSAGE,
					PLACE_HOLDER, chat.get().getFirstName(), chat.get().getLastName())));
			return;
		}

		/*
		 * retry last question
		 */
		if (RETRY_LAST_QUESTION_ANSWER.equals(update.getMessage().getText())) {
			chat = Optional.of(chatService.retryLastQuestion(chat.get()));
			question = questionService.getQuestionByChat(chat.get());
			sendQuestion(chat.get(), question, this.answersKeyboardMarkup);
			return;
		}

		/*
		 * answer to existing chat
		 */
		Optional<Response> response = responseService.getReponseByText(update.getMessage().getText());
		if (response.isPresent()) {
			question = questionService.getQuestionByChat(chat.get());
			chatAnswerService.add(chat.get(), question.get(), response.get());

			if (chat.get().getState() == ChatState.ANSWERING) {
				chat = Optional.of(chatService.updateLastAnswered(chat.get(), question.get()));
				question = questionService.getQuestionByChat(chat.get());
				sendQuestion(chat.get(), question, this.answersKeyboardMarkup);
			} else if (chat.get().getState() == ChatState.RETRY_QUESTION) {
				chatService.updateState(chat.get(), ChatState.RETRY_SENT);
				sendQuestion(chat.get(), question, this.answersKeyboardMarkup);
			}
			return;
		}

		sendTryAgain(chatId);
		return;
	}

	private void sendQuestion(Chat chat, Optional<Question> question, ReplyKeyboardMarkup replyKeyboard) {
		if (chat.getState() == ChatState.ANSWERING || chat.getState() == ChatState.RETRY_QUESTION) {
			sendMessage(new SendMessage().setChatId(chat.getId()).setText(question.get().getText())
					.setReplyMarkup(replyKeyboard));
		} else if (chat.getState() == ChatState.RETRY_SENT) {
			sendMessage(new SendMessage().setChatId(chat.getId()).setText(
					StringUtils.insertUser(RETRY_ANY_QUESTION, PLACE_HOLDER, chat.getFirstName(), chat.getLastName()))
					.setReplyMarkup(retryKeyboardMarkup));
		}
	}

	private void sendTryAgain(Long chatId) {
		Optional<Chat> chat = chatService.getChat(chatId);
		Optional<Question> question = questionService.getQuestionByChat(chat.get());
		sendMessage(new SendMessage().setChatId(chatId).setText(ERROR_MESSAGE));
		if (chat.get().getState() == ChatState.RETRY_YES) {
			sendMessage(new SendMessage().setChatId(chatId).setText(RETRY_ANY_QUESTIONS_CHOSE)
					.setReplyMarkup(questionsKeyboardMarkup));
		} else {
			sendQuestion(chat.get(), question, this.answersKeyboardMarkup);
		}
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
