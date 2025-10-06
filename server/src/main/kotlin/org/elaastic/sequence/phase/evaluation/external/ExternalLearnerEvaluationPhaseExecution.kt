package org.elaastic.sequence.phase.evaluation.external

import org.elaastic.sequence.Sequence
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.phase.evaluation.AbstractLearnerEvaluationPhaseExecution

class ExternalLearnerEvaluationPhaseExecution(
    sequence: Sequence,
    userActiveInteraction: Interaction?,
) : AbstractLearnerEvaluationPhaseExecution(
    // There is no response per se, nor a second attempt. There's no real notion of completion either
    userHasCompletedPhase2 = false,
    secondAttemptAlreadySubmitted = false,
    sequence,
    userActiveInteraction,
    lastAttemptResponse = null,
)
