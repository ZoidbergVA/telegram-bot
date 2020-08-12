ALTER TABLE `chat` ADD COLUMN `state` varchar(255);

UPDATE `chat` SET `state` = 'ANSWERING' WHERE `reported` = false AND id > 0;
UPDATE `chat` SET `state` = 'REPORTED' WHERE `reported` = true AND id > 0;

ALTER TABLE `chat` DROP COLUMN `reported`;
