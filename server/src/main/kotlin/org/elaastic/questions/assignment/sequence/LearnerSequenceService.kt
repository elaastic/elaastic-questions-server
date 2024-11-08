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

import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class LearnerSequenceService(
    @Autowired val learnerSequenceRepository: LearnerSequenceRepository,
    @Autowired val peerGradingService: PeerGradingService
) {

    fun getActiveInteractionForLearner(learnerSequence: ILearnerSequence) =
        getActiveInteractionForLearner(learnerSequence.learner, learnerSequence.sequence)

    fun getActiveInteractionForLearner(
        learner: User,
        sequence: Sequence
    ): Interaction? =
        when {
            sequence.executionIsFaceToFace() -> sequence.activeInteraction
            else -> findOrCreateLearnerSequence(learner, sequence).activeInteraction
        }

    fun getLearnerSequence(
        learner: User,
        sequence: Sequence
    ): ILearnerSequence =
        when {
            // For synchronous sequences, we do not need a persistent LearnerSequence
            sequence.executionIsFaceToFace() -> TransientLearnerSequence(learner, sequence)

            // For asynchronous sequences, we need a persistent LearnerSequence
            else -> findOrCreateLearnerSequence(learner, sequence)
        }

    fun findOrCreateLearnerSequence(
        learner: User,
        sequence: Sequence
    ): LearnerSequence =
        learnerSequenceRepository.findByLearnerAndSequence(
            learner,
            sequence
        ).let {
            it ?: LearnerSequence(learner, sequence)
                .let { learnerSequenceRepository.save(it) }
        }.let {
            if (it.activeInteraction == null && sequence.activeInteraction != null) {
                it.activeInteraction =
                    sequence.interactions[InteractionType.ResponseSubmission]
                learnerSequenceRepository.save(it)
                it
            } else it
        }

    /**
     * Count the number of reports made by the user for the given sequence
     *
     * If an error occurred when we retrieve all the peerGrading, then the
     * count is 0
     */
    fun countReportMade(user: User, sequence: Sequence): Int {
        return try {
            peerGradingService.findAllEvaluationMadeForLearner(user, sequence)
                .count { !it.reportReasons.isNullOrBlank() }
        } catch (_: IllegalStateException) {
            0
            /* If the sequence isn't initialized, an IllegalStateException will be throws
            * So if the sequence isn't initialized, the user has not been able to report something */
        }
    }
}
