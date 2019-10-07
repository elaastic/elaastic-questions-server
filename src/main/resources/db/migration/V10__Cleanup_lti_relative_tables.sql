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

drop table lti_nonce ;
drop table lti_share_key;

ALTER TABLE lms_user
    DROP FOREIGN KEY lms_user_fk3;

drop table lti_user;

ALTER TABLE lms_assignment
    DROP FOREIGN KEY lms_assignment_fk4,
    DROP FOREIGN KEY lms_assignment_lti_activity_id_context_id,
    DROP FOREIGN KEY lms_assignment_lti_course_id_lti_context_id,
    add constraint lms_assignment_lti_consumer_key
        foreign key (lti_consumer_key) references lti_consumer (consumer_key);

drop table lti_context;


