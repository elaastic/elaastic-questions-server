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

# alter table table for onboarding chapters
ALTER TABLE onboarding_state
ADD COLUMN `chapters_seen` text,
DROP COLUMN `course_page`,
DROP COLUMN `course_creation_page`,
DROP COLUMN `subject_page`,
DROP COLUMN `subject_creation_page`,
DROP COLUMN `subject_edition_page`,
DROP COLUMN `question_creation_page`,
DROP COLUMN `assignment_creation_page`,
DROP COLUMN `player_page`,
DROP COLUMN `shared_subjects_page`,
DROP COLUMN `one_shared_subject_page`;