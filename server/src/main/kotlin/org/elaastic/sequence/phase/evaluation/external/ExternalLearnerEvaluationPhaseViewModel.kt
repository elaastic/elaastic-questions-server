package org.elaastic.sequence.phase.evaluation.external

import org.elaastic.sequence.State
import org.elaastic.sequence.phase.evaluation.AbstractLearnerEvaluationPhaseViewModel
import org.elaastic.sequence.phase.response.LearnerResponseFormViewModel

class ExternalLearnerEvaluationPhaseViewModel(
    sequenceId: Long,
    interactionId: Long,
    phaseState: State,
    choices: Boolean,
    userHasCompletedPhase2: Boolean,
    secondAttemptAllowed: Boolean,
    secondAttemptAlreadySubmitted: Boolean,
    responseFormModel: LearnerResponseFormViewModel,
    evaluationExternalInstructions: String?,
) : AbstractLearnerEvaluationPhaseViewModel(
    sequenceId,
    interactionId,
    phaseState,
    choices,
    userHasCompletedPhase2,
    secondAttemptAllowed,
    secondAttemptAlreadySubmitted,
    responseFormModel
) {
    val evaluationExternalInstructions = evaluationExternalInstructions
        ?.replace(
            "(https?://|www\\.)([^\\s()\\[\\]<>]+|\\([^\\s)]*\\)|\\[[^\\s\\]]*])+(?<![.,!?])".toRegex(),
            "<a target=\"_blank\" rel=\"noreferrer\" href=\"$0\">$0</a>"
        )
}
