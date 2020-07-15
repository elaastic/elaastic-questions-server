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

# Add column to store the statement linked to a result
ALTER TABLE `choice_interaction_response`
    ADD COLUMN `statement_id` BIGINT(20);

ALTER TABLE `choice_interaction_response`
    ADD CONSTRAINT `fk_response_statement` FOREIGN KEY (`statement_id`) REFERENCES `statement` (`id`) ON DELETE CASCADE;

# Migration
UPDATE `choice_interaction_response`, `interaction`, `sequence`
SET `choice_interaction_response`.`statement_id` = `sequence`.statement_id
WHERE `choice_interaction_response`.`statement_id` IS NULL
AND `choice_interaction_response`.interaction_id = `interaction`.id
AND `interaction`.sequence_id = `sequence`.id;
