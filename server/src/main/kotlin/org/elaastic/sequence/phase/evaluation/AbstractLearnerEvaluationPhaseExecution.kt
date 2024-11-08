package org.elaastic.sequence.phase.evaluation

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.activity.response.Response
import org.elaastic.sequence.phase.LearnerPhaseExecution

abstract class AbstractLearnerEvaluationPhaseExecution(
    val userHasCompletedPhase2: Boolean,
    val secondAttemptAlreadySubmitted: Boolean,
    val sequence: Sequence,
    val userActiveInteraction: Interaction?,
    val lastAttemptResponse: Response?
) : LearnerPhaseExecution {
}