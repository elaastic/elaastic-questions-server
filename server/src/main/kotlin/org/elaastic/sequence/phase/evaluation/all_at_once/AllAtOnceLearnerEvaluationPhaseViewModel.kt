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

import org.elaastic.sequence.State
import org.elaastic.sequence.phase.evaluation.AbstractLearnerEvaluationPhaseViewModel
import org.elaastic.sequence.phase.evaluation.ResponseData
import org.elaastic.sequence.phase.response.LearnerResponseFormViewModel

class AllAtOnceLearnerEvaluationPhaseViewModel(
    sequenceId: Long,
    interactionId: Long,
    phaseState: State,
    choices: Boolean,
    userHasCompletedPhase2: Boolean,
    val userHasPerformedEvaluation: Boolean,
    val responsesToGrade: List<ResponseData>,
    secondAttemptAllowed: Boolean,
    secondAttemptAlreadySubmitted: Boolean,
    responseFormModel: LearnerResponseFormViewModel
) : AbstractLearnerEvaluationPhaseViewModel(
    sequenceId,
    interactionId,
    phaseState,
    choices,
    userHasCompletedPhase2,
    secondAttemptAllowed,
    secondAttemptAlreadySubmitted,
    responseFormModel
)