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

-- Add new columns for assignment
ALTER TABLE `subject`
    ADD COLUMN `parent_subject_id` BIGINT(20) DEFAULT NULL,
    ADD CONSTRAINT `fk_parent_subject` FOREIGN KEY (`parent_subject_id`) REFERENCES `subject` (`id`) ON DELETE SET NULL;