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

package org.elaastic.sequence.phase.evaluation.draxo

import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.response.ResponseService
import org.elaastic.sequence.phase.LearnerPhase
import org.elaastic.sequence.phase.LearnerPhaseExecution
import org.elaastic.sequence.phase.evaluation.AbstractLearnerEvaluationPhaseExecutionLoader
import org.elaastic.sequence.phase.evaluation.ResponseData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("DraxoLearnerEvaluationPhaseExecutionLoader")
class DraxoLearnerEvaluationPhaseExecutionLoader(
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val responseService: ResponseService,
) : AbstractLearnerEvaluationPhaseExecutionLoader() {

    override fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution = run {
        require(learnerPhase is DraxoLearnerEvaluationPhase) {
            "LearnerResponsePhaseExecutionService only handle DraxoLearnerEvaluationPhase interaction ; provided: ${learnerPhase.javaClass}"
        }

        val sequence = learnerPhase.learnerSequence.sequence
        val learner = learnerPhase.learnerSequence.learner

        val secondAttemptAlreadySubmitted = responseService.hasResponseForUser(learner, sequence, 2)

        val responseToEvaluateCount = sequence.getEvaluationSpecification().responseToEvaluateCount

        val responseIdAlreadyGradedList =
            peerGradingService.findAllEvaluation(learner, sequence).map { it.response.id!! }

        val nextResponseToGrade =
            if (responseIdAlreadyGradedList.size < responseToEvaluateCount) {
                responseService.findNextResponseToGrade(
                    sequence = sequence,
                    attempt = sequence.whichAttemptEvaluate(),
                    user = learner,
                    excludedIds = responseIdAlreadyGradedList,
                )?.let { ResponseData(it) }
            } else null

        return DraxoLearnerEvaluationPhaseExecution(
            userHasCompletedPhase2 = nextResponseToGrade == null,
            secondAttemptAlreadySubmitted = secondAttemptAlreadySubmitted,
            nextResponseToGrade = nextResponseToGrade,
            lastResponseToGrade = responseIdAlreadyGradedList.size == responseToEvaluateCount - 1,
            sequence = learnerPhase.learnerSequence.sequence,
            userActiveInteraction = learnerPhase.learnerSequence.activeInteraction,
            lastAttemptResponse = responseService.find(learner, sequence, 2) ?: responseService.find(
                learner,
                sequence,
                1
            ),
        )
    }


}