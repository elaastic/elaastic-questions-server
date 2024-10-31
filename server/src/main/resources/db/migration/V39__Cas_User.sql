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

CREATE TABLE `cas_user`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `elaastic_user_id` bigint(20) NOT NULL,
    `cas_key`          varchar(255) NOT NULL,
    `cas_user_id`      varchar(255) NOT NULL,
    `created_at`       datetime     NOT NULL,
    PRIMARY KEY (`id`),
    KEY (`cas_key`, `cas_user_id`),
    CONSTRAINT `cas_user_fk1` FOREIGN KEY (`elaastic_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
