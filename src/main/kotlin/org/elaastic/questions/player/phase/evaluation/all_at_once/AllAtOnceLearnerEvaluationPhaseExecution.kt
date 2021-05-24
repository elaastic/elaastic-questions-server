package org.elaastic.questions.player.phase.evaluation.all_at_once

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseExecution
import org.elaastic.questions.player.phase.evaluation.ResponseData

class AllAtOnceLearnerEvaluationPhaseExecution(
    val userHasCompletedPhase2: Boolean,
    val userHasPerformedEvaluation: Boolean,
    val secondAttemptAlreadySubmitted: Boolean,
    val responsesToGrade: List<ResponseData>,
    val sequence: Sequence,
    val userActiveInteraction: Interaction?,
    val firstAttemptResponse: Response?
): AbstractLearnerEvaluationPhaseExecution() {
}