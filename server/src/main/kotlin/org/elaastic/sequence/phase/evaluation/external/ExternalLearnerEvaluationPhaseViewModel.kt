package org.elaastic.sequence.phase.evaluation.external

import org.elaastic.sequence.State
import org.elaastic.sequence.phase.evaluation.AbstractLearnerEvaluationPhaseViewModel
import org.elaastic.sequence.phase.response.LearnerResponseFormViewModel

class ExternalLearnerEvaluationPhaseViewModel(
    sequenceId: Long,
    interactionId: Long,
    phaseState: State,
    choices: Boolean,
    responseFormModel: LearnerResponseFormViewModel,
) : AbstractLearnerEvaluationPhaseViewModel(
    sequenceId,
    interactionId,
    phaseState,
    choices,
    userHasCompletedPhase2 = false,
    secondAttemptAllowed = false,
    secondAttemptAlreadySubmitted = false,
    responseFormModel
)
