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

ALTER TABLE `assignment` MODIFY `global_id` CHAR(36);
ALTER TABLE `assignment` RENAME COLUMN `global_id` TO `uuid`;
ALTER TABLE `assignment` ADD UNIQUE(`uuid`);

ALTER TABLE `course`  MODIFY `global_id` CHAR(36);
ALTER TABLE `course` RENAME COLUMN `global_id` TO `uuid`;
ALTER TABLE `course` ADD UNIQUE(`uuid`);

ALTER TABLE `subject`  MODIFY `global_id` CHAR(36);
ALTER TABLE `subject` RENAME COLUMN `global_id` TO `uuid`;
ALTER TABLE `subject` ADD UNIQUE(`uuid`);

ALTER TABLE `attachement` ADD COLUMN `uuid` CHAR(36) NULL UNIQUE ;
ALTER TABLE `choice_interaction_response`  ADD COLUMN `uuid` CHAR(36) NULL UNIQUE ;
ALTER TABLE `interaction`  ADD COLUMN `uuid` CHAR(36) NULL UNIQUE;
ALTER TABLE `sequence`  ADD COLUMN `uuid` CHAR(36) NULL UNIQUE;
ALTER TABLE `statement`  ADD COLUMN `uuid` CHAR(36) NULL UNIQUE;
ALTER TABLE `user`  ADD COLUMN `uuid` CHAR(36) NULL UNIQUE;




