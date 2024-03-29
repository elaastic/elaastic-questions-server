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

CREATE TABLE `course`(
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `version` bigint(20) NOT NULL,
    `date_created` datetime NOT NULL,
    `title` text NOT NULL,
    `owner_id` bigint(20) NOT NULL,
    `last_updated` datetime NOT NULL,
    `global_id` text NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_course_owner_id` (`owner_id`),
    CONSTRAINT `fk_course_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `subject`
    ADD COLUMN `course_id` bigint(20) DEFAULT NULL,
    ADD CONSTRAINT `fk_subject_course_id` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)  ON DELETE CASCADE;

/*ALTER TABLE `subject`
    DROP COLUMN course;*/