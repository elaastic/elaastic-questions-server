/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.sequence.phase.evaluation.all_at_once

import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.response.ResponseService
import org.elaastic.sequence.phase.LearnerPhase
import org.elaastic.sequence.phase.LearnerPhaseExecution
import org.elaastic.sequence.phase.evaluation.AbstractLearnerEvaluationPhaseExecutionLoader
import org.elaastic.sequence.phase.evaluation.ResponseData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("AllAtOnceLearnerEvaluationPhaseExecutionLoader")
class AllAtOnceLearnerEvaluationPhaseExecutionLoader(
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val responseService: ResponseService,
) : AbstractLearnerEvaluationPhaseExecutionLoader() {

    override fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution = run {
        require(learnerPhase is AllAtOnceLearnerEvaluationPhase) {
            "LearnerResponsePhaseExecutionService only handle AllAtOnceLearnerEvaluationPhase interaction ; provided: ${learnerPhase.javaClass}"
        }

        val sequence = learnerPhase.learnerSequence.sequence
        val learner = learnerPhase.learnerSequence.learner

        val userHasPerformedEvaluation = peerGradingService.userHasPerformedEvaluation(learner, sequence)

        val secondAttemptAlreadySubmitted = responseService.hasResponseForUser(learner, sequence, 2)

        val responsesToGrade = if (!userHasPerformedEvaluation)
            responseService.findAllRecommandedResponsesForUser(
                sequence = sequence,
                attempt = sequence.whichAttemptEvaluate(),
                user = learner
            ).map { ResponseData(it) }
        else listOf()

        AllAtOnceLearnerEvaluationPhaseExecution(
            userHasCompletedPhase2 = (responsesToGrade.isEmpty() &&
                    (secondAttemptAlreadySubmitted || !sequence.isSecondAttemptAllowed())),
            userHasPerformedEvaluation = userHasPerformedEvaluation,
            secondAttemptAlreadySubmitted = secondAttemptAlreadySubmitted,
            responsesToGrade = responsesToGrade,
            sequence = learnerPhase.learnerSequence.sequence,
            userActiveInteraction = learnerPhase.learnerSequence.activeInteraction,
            firstAttemptResponse = responseService.find(learner, sequence),
        )
    }


}