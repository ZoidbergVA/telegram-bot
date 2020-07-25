CREATE TABLE `question` (
  `id` int(11) NOT NULL,
  `question_text` varchar(2047) NOT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `chat` (
  `id` int(11) NOT NULL,
  `last_answered_question_id` int(11),
  `first_name` varchar(255),
  `last_name` varchar(255),
  `user_name` varchar(255),
  `reported` boolean,
  PRIMARY KEY (`id`),
  CONSTRAINT `chat_question_fk` FOREIGN KEY (`last_answered_question_id`) REFERENCES `question` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE `chat_answer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `chat_id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `answer_text` varchar(2047) NOT NULL,
  `answer_weight` int(11) NOT NULL,
  `date_created` TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `chat_answer_chat` FOREIGN KEY (`chat_id`) REFERENCES `chat` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `chat_answer_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);