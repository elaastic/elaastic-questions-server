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

# remove doublons in peer_grading table

DELETE pg1
FROM peer_grading pg1
         LEFT JOIN (
    SELECT grader_id, response_id, MAX(id) as max_id
    FROM peer_grading
    GROUP BY grader_id, response_id
) pg2 ON pg1.grader_id = pg2.grader_id AND pg1.response_id = pg2.response_id
WHERE pg1.id < pg2.max_id;

# add DRAXO colums

ALTER TABLE `peer_grading` ADD COLUMN `type` VARCHAR(12) NOT NULL DEFAULT 'LIKERT';
ALTER TABLE `peer_grading` ADD COLUMN `criteria_D` VARCHAR(12);
ALTER TABLE `peer_grading` ADD COLUMN `criteria_R` VARCHAR(12);
ALTER TABLE `peer_grading` ADD COLUMN `criteria_A` VARCHAR(12);
ALTER TABLE `peer_grading` ADD COLUMN `criteria_X` VARCHAR(12);
ALTER TABLE `peer_grading` ADD COLUMN `criteria_O` VARCHAR(12);

# add unique constraint

ALTER TABLE `peer_grading` ADD CONSTRAINT uc_user_response UNIQUE (`grader_id`, `response_id`);



