package org.elaastic.questions.player.phase.evaluation.one_by_one

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseExecution
import org.elaastic.questions.player.phase.evaluation.ResponseData

class OneByOneLearnerEvaluationPhaseExecution(
    userHasCompletedPhase2: Boolean,
    secondAttemptAlreadySubmitted: Boolean,
    val nextResponseToGrade: ResponseData?,
    sequence: Sequence,
    userActiveInteraction: Interaction?,
    firstAttemptResponse: Response?
): AbstractLearnerEvaluationPhaseExecution(
    userHasCompletedPhase2,
    secondAttemptAlreadySubmitted,
    sequence,
    userActiveInteraction,
    firstAttemptResponse
) {
}