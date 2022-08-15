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

ALTER TABLE user
    ADD COLUMN `source` varchar(32) NOT NULL default 'ELAASTIC';

UPDATE user
SET `source` = 'ANONYMOUS'
WHERE is_anonymous is true;

UPDATE user
SET `source` = 'LMS'
WHERE EXISTS(
              SELECT 1
              FROM lms_user
              WHERE tsaap_user_id = user.id
          );


UPDATE user
SET `source` = 'CAS'
WHERE EXISTS(
              SELECT 1
              FROM cas_user
              WHERE cas_user.elaastic_user_id = user.id
          );

ALTER TABLE user
DROP COLUMN is_anonymous;