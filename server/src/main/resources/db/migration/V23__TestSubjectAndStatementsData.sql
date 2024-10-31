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

INSERT INTO `subject` (`id`, `version`, `date_created`, `title`, `course`, `owner_id`, `last_updated`, `global_id`)
VALUES (1,0,'2020-06-06 10:26:16','Sujet test','',359, '2020-06-06 10:26:16','c71b94b6-ad03-25a9-06d4-00163e3774aa');
# owner id 359 to match statements linked

UPDATE `statement`
SET subject_id = 1
WHERE `id`= 618
    OR `id`= 619
    OR `id`= 620
    OR `id`= 621
    OR `id`= 622;

UPDATE `assignment`
SET subject_id = 1
WHERE `id`= 382;
