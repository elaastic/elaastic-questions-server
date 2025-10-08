package org.elaastic.sequence.phase.evaluation.external

import org.elaastic.activity.response.Response
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.phase.evaluation.AbstractLearnerEvaluationPhaseExecution

class ExternalLearnerEvaluationPhaseExecution(
    userHasCompletedPhase2: Boolean,
    secondAttemptAlreadySubmitted: Boolean,
    sequence: Sequence,
    userActiveInteraction: Interaction?,
    lastAttemptResponse: Response?,
) : AbstractLearnerEvaluationPhaseExecution(
    userHasCompletedPhase2,
    secondAttemptAlreadySubmitted,
    sequence,
    userActiveInteraction,
    lastAttemptResponse,
)
