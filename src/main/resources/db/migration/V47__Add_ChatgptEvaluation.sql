#
# Elaastic - formative assessment system
# Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

CREATE TABLE `chatgpt_evaluation`(
     `id` bigint(20) NOT NULL AUTO_INCREMENT,
     `version` bigint(20) NOT NULL,
     `date_created` datetime NOT NULL,
     `last_updated` datetime NOT NULL,
     `annotation` text,
     `grade` decimal(2,1),
     `reported_by_student` bool NOT NULL,
     `hidden_by_teacher` bool NOT NULL,
     `removed_by_teacher` bool NOT NULL,
     `response_id` bigint(20) NOT NULL,
     PRIMARY KEY (`id`),
     UNIQUE KEY `idx_chatgpt_evaluation_response_id` (`response_id`),
     CONSTRAINT fk_chat_gpt_evaluation_id FOREIGN KEY (response_id) REFERENCES choice_interaction_response (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `chatgpt_prompt`(
     `id` bigint(20) NOT NULL AUTO_INCREMENT,
     `version` bigint(20) NOT NULL,
     `start_date` datetime NOT NULL,
     `end_date` datetime NOT NULL,
     `active` bool NOT NULL,
     `content` text NOT NULL,
     `language` varchar(16) DEFAULT 'fr' NOT NULL,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `chatgpt_prompt`
    MODIFY COLUMN `end_date` datetime DEFAULT NULL;

ALTER TABLE `chatgpt_evaluation`
    ADD COLUMN `status` varchar(32) DEFAULT NULL;

RENAME TABLE chatgpt_evaluation TO chat_gpt_evaluation;
ALTER TABLE chat_gpt_evaluation RENAME INDEX idx_chatgpt_evaluation_response_id TO idx_chat_gpt_evaluation_response_id;

RENAME TABLE chatgpt_prompt TO chat_gpt_prompt;

ALTER TABLE sequence ADD COLUMN `chat_gpt_evaluation_enabled` boolean NOT NULL DEFAULT false;

ALTER TABLE chat_gpt_evaluation DROP COLUMN reported_by_student;

ALTER TABLE chat_gpt_evaluation ADD COLUMN report_reason varchar(64) DEFAULT NULL;
ALTER TABLE chat_gpt_evaluation ADD COLUMN report_comment text DEFAULT NULL;
ALTER TABLE chat_gpt_evaluation ADD COLUMN utility_grade text DEFAULT NULL;

ALTER TABLE chat_gpt_evaluation CHANGE COLUMN report_reason report_reasons TEXT;
ALTER TABLE chat_gpt_evaluation MODIFY COLUMN utility_grade TINYINT(2);
ALTER TABLE chat_gpt_prompt ADD CONSTRAINT unique_prompt_active_by_language UNIQUE (language, active);
