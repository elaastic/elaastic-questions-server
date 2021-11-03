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

# Add table for onboarding chapters (0 = chapter has not been not played yet, 1 = chapter has been played)
CREATE TABLE `onboarding_state` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20) NOT NULL,
    `course_page` bit(1) NOT NULL DEFAULT b'0',
    `course_creation_page` bit(1) NOT NULL DEFAULT b'0',
    `subject_page` bit(1) NOT NULL DEFAULT b'0',
    `subject_creation_page` bit(1) NOT NULL DEFAULT b'0',
    `subject_edition_page` bit(1) NOT NULL DEFAULT b'0',
    `question_creation_page` bit(1) NOT NULL DEFAULT b'0',
    `assignment_creation_page` bit(1) NOT NULL DEFAULT b'0',
    `player_page` bit(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (`id`),
    KEY `idx_onboarding_state_user_id` (`user_id`),
    CONSTRAINT `fk_onboarding_state_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
