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

create table terms
(
    id        bigint auto_increment
        primary key,
    version   int                                  not null,
    start_date datetime   default CURRENT_TIMESTAMP not null,
    end_date   datetime                             null,
    is_active tinyint(1) default 1                 not null
);

create table terms_content
(
    id       bigint auto_increment
        primary key,
    content  text                     not null,
    language varchar(16) default 'fr' not null,
    terms_id bigint                   not null,
    constraint terms_content_terms_fk
        foreign key (terms_id) references terms (id)
            on delete cascade
);

create table user_consent
(
    id           bigint auto_increment
        primary key,
    collect_date datetime default CURRENT_TIMESTAMP not null,
    username      varchar(255)                       not null,
    terms_id     bigint                             not null,
    constraint user_consent_unique
        unique (username, terms_id),
    constraint user_consent_terms_fk
        foreign key (terms_id) references terms (id)
            on delete cascade
);

