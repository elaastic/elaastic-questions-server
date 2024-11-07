package org.elaastic.sequence.phase.evaluation.all_at_once

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.response.Response
import org.elaastic.sequence.phase.evaluation.AbstractLearnerEvaluationPhaseExecution
import org.elaastic.sequence.phase.evaluation.ResponseData

class AllAtOnceLearnerEvaluationPhaseExecution(
    userHasCompletedPhase2: Boolean,
    val userHasPerformedEvaluation: Boolean,
    secondAttemptAlreadySubmitted: Boolean,
    val responsesToGrade: List<ResponseData>,
    sequence: Sequence,
    userActiveInteraction: Interaction?,
    firstAttemptResponse: Response?
) : AbstractLearnerEvaluationPhaseExecution(
    userHasCompletedPhase2,
    secondAttemptAlreadySubmitted,
    sequence,
    userActiveInteraction,
    firstAttemptResponse
) {
}