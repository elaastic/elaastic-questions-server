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

package org.elaastic.sequence.phase

import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.State
import org.elaastic.sequence.phase.descriptor.PhaseDescriptor
import org.elaastic.sequence.phase.evaluation.EvaluationPhaseConfig
import org.elaastic.sequence.phase.evaluation.all_at_once.AllAtOnceLearnerEvaluationPhase
import org.elaastic.sequence.phase.evaluation.draxo.DraxoLearnerEvaluationPhase
import org.elaastic.sequence.phase.response.LearnerResponsePhase
import org.elaastic.sequence.phase.result.LearnerResultPhase
import org.springframework.stereotype.Service

/** @see LearnerPhase */
@Service
class LearnerPhaseFactory {

    fun build(
        phaseDescriptor: PhaseDescriptor,
        learnerSequence: ILearnerSequence,
        phaseIndex: Int,
        active: Boolean,
        state: State,
    ): LearnerPhase = when (phaseDescriptor.type) {
        LearnerPhaseType.RESPONSE -> LearnerResponsePhase(learnerSequence, phaseIndex, active, state)
        LearnerPhaseType.EVALUATION ->
            when (learnerSequence.sequence.evaluationPhaseConfig) {
                EvaluationPhaseConfig.ALL_AT_ONCE -> AllAtOnceLearnerEvaluationPhase(
                    learnerSequence,
                    phaseIndex,
                    active,
                    state,
                )

                EvaluationPhaseConfig.DRAXO -> DraxoLearnerEvaluationPhase(
                    learnerSequence,
                    phaseIndex,
                    active,
                    state
                )
            }

        LearnerPhaseType.RESULT -> LearnerResultPhase(learnerSequence, phaseIndex, active, state)
    }
}