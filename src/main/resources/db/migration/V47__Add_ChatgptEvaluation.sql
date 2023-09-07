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

CREATE TABLE `chat_gpt_evaluation`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT,
    `version`            bigint(20) NOT NULL,
    `status`             varchar(32) DEFAULT NULL,
    `date_created`       datetime   NOT NULL,
    `last_updated`       datetime   NOT NULL,
    `annotation`         text,
    `grade`              decimal(2, 1),
    `hidden_by_teacher`  bool       NOT NULL,
    `removed_by_teacher` bool       NOT NULL,
    `response_id`        bigint(20) NOT NULL,
    `report_reasons`     TEXT        DEFAULT NULL,
    `report_comment`     TEXT        DEFAULT NULL,
    `utility_grade`      TINYINT(2)  DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_chat_gpt_evaluation_response_id` (`response_id`),
    CONSTRAINT fk_chat_gpt_evaluation_id FOREIGN KEY (response_id) REFERENCES choice_interaction_response (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `chat_gpt_prompt`
(
    `id`         bigint(20)               NOT NULL AUTO_INCREMENT,
    `version`    bigint(20)               NOT NULL,
    `start_date` datetime                 NOT NULL,
    `end_date`   datetime    DEFAULT NULL,
    `active`     bool                     NOT NULL,
    `content`    text                     NOT NULL,
    `language`   varchar(16) DEFAULT 'fr' NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT unique_prompt_active_by_language UNIQUE (language, active)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE sequence
    ADD COLUMN `chat_gpt_evaluation_enabled` boolean NOT NULL DEFAULT false;

# Add prompts
INSERT INTO `chat_gpt_prompt` (`version`, `start_date`, `end_date`, `active`, `content`, `language`)
VALUES (1,
        now(),
        NULL,
        TRUE,
        'Tu es un enseignant indulgent et tu dois évaluer réponse d''un étudiant à une question. Pour t''aider à évaluer et à comprendre le contexte de la réponse tu as le contenu de la question, le titre de la question, la réponse donné par un autre enseignant et la réponse de l''étudiant. Tu ne dois ni citer ni répéter la réponse de l’enseignant. Ton évaluation doit être argumentée et contenir 8 lignes de texte au maximum. Sans donner de détails, après ton explication tu noteras la réponse de l''étudiant de 0 à 5 entre crochet sous cette forme : "Note : []".\n Titre de la question : $\{title}.\n Contenu de la question : $\{questionContent}.\n Réponse de l''enseignant: $\{teacherExplanation}.\n Réponse de l''étudiant: $\{studentExplanation}.',
        'fr');

INSERT INTO `chat_gpt_prompt` (`version`, `start_date`, `end_date`, `active`, `content`, `language`)
VALUES (1,
        now(),
        NULL,
        TRUE,
        'You are a lenient teacher and you need to evaluate a student''s answer to a question. To assist you in evaluating and understanding the context of the answer, you have the content of the question, the title of the question, the answer given by another teacher, and the student''s response. You must neither quote nor repeat the teacher''s answer. Your evaluation should be supported with reasons and must be 8 lines of text at most. Without giving specifics, after your explanation, you will score the student''s answer from 0 to 5 in brackets like this: "Score: []".\n Question Title: $\{title}.\n Content of the question: $\{questionContent}.\n Teacher''s Answer: $\{teacherExplanation}.\n Student''s Response: $\{studentExplanation}.',
        'en');

