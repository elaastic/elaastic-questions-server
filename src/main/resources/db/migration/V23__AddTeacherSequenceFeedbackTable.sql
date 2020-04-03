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
# along with this program.  If not, see https://www.gnu.org/licenses/.
#

CREATE TABLE sequence_feedback_teacher (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    date_created datetime NOT NULL,
    version bigint(20) NOT NULL,
    teacher_id bigint(20) NOT NULL,
    sequence_id bigint(20) NOT NULL,
    recommend_rating tinyint(1) NOT NULL,
    reuse_rating tinyint(1) NOT NULL,
    explanation text NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sequence_teacher_feedback_teacher FOREIGN KEY (teacher_id) REFERENCES user (id),
    CONSTRAINT fk_sequence_teacher_feedback_sequence FOREIGN KEY (sequence_id) REFERENCES sequence (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
