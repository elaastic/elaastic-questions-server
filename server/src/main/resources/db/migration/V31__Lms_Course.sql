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

CREATE TABLE `lms_course`
(
    `id` bigint(20) auto_increment primary key,
    `course_id`        bigint(20)   NOT NULL,
    `lti_course_id`    varchar(255) NOT NULL,
    `lti_course_title` text NOT NULL,
    `lti_consumer_key` varchar(255) NOT NULL,
    `source`           varchar(255) DEFAULT NULL,
    constraint unique_lms_course unique (`course_id`, `lti_course_id`, `lti_consumer_key`),
    CONSTRAINT `lms_course_lti_consumer_key` FOREIGN KEY (`lti_consumer_key`) REFERENCES `lti_consumer` (`consumer_key`),
    CONSTRAINT `lms_course_course_key` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;