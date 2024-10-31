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

# Temporary renames CHAR(36) uuid columns
ALTER TABLE `assignment` RENAME COLUMN `uuid` TO `char_uuid`;
ALTER TABLE `course` RENAME COLUMN `uuid` TO `char_uuid`;
ALTER TABLE `subject` RENAME COLUMN `uuid` TO `char_uuid`;
ALTER TABLE `attachement` RENAME COLUMN `uuid` TO `char_uuid`;
ALTER TABLE `choice_interaction_response`  RENAME COLUMN `uuid` TO `char_uuid`;
ALTER TABLE `interaction` RENAME COLUMN `uuid` TO `char_uuid`;
ALTER TABLE `sequence` RENAME COLUMN `uuid` TO `char_uuid`;
ALTER TABLE `statement` RENAME COLUMN `uuid` TO `char_uuid`;
ALTER TABLE `user` RENAME COLUMN `uuid` TO `char_uuid`;

# Create new BINARY(16) uuid columns
ALTER TABLE `assignment` ADD COLUMN `uuid` BINARY(16) UNIQUE;
ALTER TABLE `course`  ADD COLUMN `uuid` BINARY(16) UNIQUE;
ALTER TABLE `subject`  ADD COLUMN `uuid` BINARY(16) UNIQUE;
ALTER TABLE `attachement`  ADD COLUMN `uuid` BINARY(16) UNIQUE;
ALTER TABLE `choice_interaction_response`   ADD COLUMN `uuid` BINARY(16) UNIQUE;
ALTER TABLE `interaction`  ADD COLUMN `uuid` BINARY(16) UNIQUE;
ALTER TABLE `sequence`  ADD COLUMN `uuid` BINARY(16) UNIQUE;
ALTER TABLE `statement`  ADD COLUMN `uuid` BINARY(16) UNIQUE;
ALTER TABLE `user`  ADD COLUMN `uuid` BINARY(16) UNIQUE;

# Migrate UUIDS from CHAR(36) to BINARY(16)
UPDATE `assignment` SET uuid = UNHEX(REPLACE(char_uuid, '-', ''));
UPDATE `course` SET uuid = UNHEX(REPLACE(char_uuid, '-', ''));
UPDATE `subject` SET uuid = UNHEX(REPLACE(char_uuid, '-', ''));
UPDATE `attachement` SET uuid = UNHEX(REPLACE(char_uuid, '-', ''));
UPDATE `choice_interaction_response` SET uuid = UNHEX(REPLACE(char_uuid, '-', ''));
UPDATE `interaction` SET uuid = UNHEX(REPLACE(char_uuid, '-', ''));
UPDATE `sequence` SET uuid = UNHEX(REPLACE(char_uuid, '-', ''));
UPDATE `statement` SET uuid = UNHEX(REPLACE(char_uuid, '-', ''));
UPDATE `user` SET uuid = UNHEX(REPLACE(char_uuid, '-', ''));

# Add NOT NULL constraint
ALTER TABLE `assignment` MODIFY COLUMN `uuid` BINARY(16) NOT NULL;
ALTER TABLE `course`  MODIFY `uuid` BINARY(16) NOT NULL;
ALTER TABLE `subject`  MODIFY `uuid` BINARY(16) NOT NULL;
ALTER TABLE `attachement`  MODIFY `uuid` BINARY(16) NOT NULL;
ALTER TABLE `choice_interaction_response` MODIFY `uuid` BINARY(16) NOT NULL;
ALTER TABLE `interaction`  MODIFY `uuid` BINARY(16) NOT NULL;
ALTER TABLE `sequence`  MODIFY `uuid` BINARY(16) NOT NULL;
ALTER TABLE `statement`  MODIFY `uuid` BINARY(16) NOT NULL;
ALTER TABLE `user`  MODIFY `uuid` BINARY(16) NOT NULL;

# Remove old columns
ALTER TABLE `assignment` DROP COLUMN `char_uuid`;
ALTER TABLE `course` DROP COLUMN `char_uuid`;
ALTER TABLE `subject` DROP COLUMN `char_uuid`;
ALTER TABLE `attachement` DROP COLUMN  `char_uuid`;
ALTER TABLE `choice_interaction_response`  DROP COLUMN `char_uuid`;
ALTER TABLE `interaction` DROP COLUMN `char_uuid`;
ALTER TABLE `sequence` DROP COLUMN `char_uuid`;
ALTER TABLE `statement` DROP COLUMN `char_uuid`;
ALTER TABLE `user` DROP COLUMN `char_uuid`;




