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


ALTER TABLE `peer_grading`
    ADD CONSTRAINT `fk_peer_grading_response_id` FOREIGN KEY (`response_id`) REFERENCES `choice_interaction_response` (`id`) ON DELETE CASCADE ;

ALTER TABLE `choice_interaction_response`
    DROP FOREIGN KEY `fk_choice_interaction_response_interaction`;

ALTER TABLE `choice_interaction_response`
    ADD CONSTRAINT `fk_choice_interaction_response_interaction` FOREIGN KEY (`interaction_id`) REFERENCES `interaction` (`id`) ON DELETE CASCADE;

ALTER TABLE `interaction`
    ADD CONSTRAINT `fk_interaction_sequence` FOREIGN KEY (`sequence_id`) REFERENCES `sequence` (`id`) ON DELETE CASCADE ;

ALTER TABLE `learner_sequence`
    DROP FOREIGN KEY `fk_learner_sequence_active_interaction`;

ALTER TABLE `learner_sequence`
    ADD CONSTRAINT `fk_learner_sequence_active_interaction` FOREIGN KEY (`active_interaction_id`) REFERENCES `interaction` (`id`) ON DELETE SET NULL ;

ALTER TABLE `learner_sequence`
    DROP FOREIGN KEY `fk_learner_sequence_sequence`;

ALTER TABLE `learner_sequence`
    ADD CONSTRAINT `fk_learner_sequence_sequence` FOREIGN KEY (`sequence_id`) REFERENCES `sequence` (`id`) ON DELETE CASCADE ;
