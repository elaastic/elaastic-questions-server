package org.elaastic.questions.player.phase.evaluation.one_by_one

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseExecution
import org.elaastic.questions.player.phase.evaluation.ResponseData

class OneByOneLearnerEvaluationPhaseExecution(
    val userHasCompletedPhase2: Boolean,
    val secondAttemptAlreadySubmitted: Boolean,
    val nextResponseToGrade: ResponseData?,
    val sequence: Sequence,
    val userActiveInteraction: Interaction?,
    val firstAttemptResponse: Response?
): AbstractLearnerEvaluationPhaseExecution() {
}