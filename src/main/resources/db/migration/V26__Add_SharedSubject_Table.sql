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

CREATE TABLE `shared_subject` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `version` bigint(20) NOT NULL,
    `date_created` datetime NOT NULL,
    `last_updated` datetime NOT NULL,
    `teacher_id` bigint(20) NOT NULL,
    `subject_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `learner_assignment_unique` (`teacher_id`,`subject_id`),
    KEY `idx_shared_subject_teacher_id` (`teacher_id`),
    KEY `idx_shared_subject_subject_id` (`subject_id`),
    CONSTRAINT `fk_shared_subject_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_shared_subject_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;