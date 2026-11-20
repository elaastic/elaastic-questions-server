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

import org.elaastic.activity.evaluation.peergrading.draxo.DraxoEvaluation
import org.elaastic.activity.response.Response
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.phase.evaluation.AbstractLearnerEvaluationPhaseExecution
import org.elaastic.sequence.phase.evaluation.ResponseData

class DraxoLearnerEvaluationPhaseExecution(
    userHasCompletedPhase2: Boolean,
    secondAttemptAlreadySubmitted: Boolean,
    val nextResponseToGrade: ResponseData?,
    val lastResponseToGrade: Boolean,
    sequence: Sequence,
    userActiveInteraction: Interaction?,
    lastAttemptResponse: Response?,
    val draxoEvaluation: DraxoEvaluation = DraxoEvaluation()
) : AbstractLearnerEvaluationPhaseExecution(
    userHasCompletedPhase2,
    secondAttemptAlreadySubmitted,
    sequence,
    userActiveInteraction,
    lastAttemptResponse
) {
}