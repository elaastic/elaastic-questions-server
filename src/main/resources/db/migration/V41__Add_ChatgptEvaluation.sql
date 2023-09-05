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
     CONSTRAINT `fk_chatgpt_evaluation_id` FOREIGN KEY (`response_id`) REFERENCES `choice_interaction_response` (`id`)
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