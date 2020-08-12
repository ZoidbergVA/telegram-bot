UPDATE `chat` SET `last_answered_question_id` = NULL WHERE id > 0;

DROP TABLE `chat_answer`;

CREATE TABLE `chat_answer` (
  `chat_id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `answer_text` varchar(2047) NOT NULL,
  `answer_weight` int(11) NOT NULL,
  `date_created` TIMESTAMP,
  PRIMARY KEY (`chat_id`, `question_id`),
  CONSTRAINT `chat_answer_chat` FOREIGN KEY (`chat_id`) REFERENCES `chat` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `chat_answer_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);