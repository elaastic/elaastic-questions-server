/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class LearnerSequenceService(
        @Autowired val learnerSequenceRepository: LearnerSequenceRepository
) {

    fun getActiveInteractionForLearner(learner: User,
                                       sequence: Sequence): Interaction? =
            when {
                sequence.executionIsFaceToFace() -> sequence.activeInteraction
                else -> findOrCreateLearnerSequence(learner, sequence).activeInteraction
            }

    fun findOrCreateLearnerSequence(learner: User,
                                    sequence: Sequence) : LearnerSequence =
            learnerSequenceRepository.findByLearnerAndSequence(
                    learner,
                    sequence
            ).let {
                it ?: LearnerSequence(learner, sequence)
                        .let { learnerSequenceRepository.save(it) }
            }.let {
                if (it.activeInteraction == null && sequence.activeInteraction != null) {
                    it.activeInteraction = sequence.interactions[InteractionType.ResponseSubmission]
                    learnerSequenceRepository.save(it)
                }
                else it
            }
}
