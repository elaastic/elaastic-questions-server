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

RENAME TABLE action to event_log;

ALTER TABLE eventLog
    ADD COLUMN user_id BIGINT(20) AFTER sequence_id;

UPDATE eventLog
SET user_id = (SELECT owner_id
                    FROM sequence
                    WHERE eventLog.sequence_id = sequence.id)
WHERE user_id is NULL;

ALTER TABLE sequence
    ADD COLUMN phase_2_skipped bit(1) NOT NULL DEFAULT b'0';