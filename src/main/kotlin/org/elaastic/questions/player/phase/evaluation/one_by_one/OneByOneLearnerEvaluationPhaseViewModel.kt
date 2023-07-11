package org.elaastic.questions.player.phase.evaluation.one_by_one

import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseViewModel
import org.elaastic.questions.player.phase.evaluation.ResponseData
import org.elaastic.questions.player.phase.response.LearnerResponseFormViewModel

class OneByOneLearnerEvaluationPhaseViewModel(
    sequenceId: Long,
    interactionId: Long,
    phaseState: State,
    choices: Boolean,
    userHasCompletedPhase2: Boolean,
    val nextResponseToGrade: ResponseData?,
    val lastResponseToGrade: Boolean,
    secondAttemptAllowed: Boolean,
    secondAttemptAlreadySubmitted: Boolean,
    responseFormModel: LearnerResponseFormViewModel
)  : AbstractLearnerEvaluationPhaseViewModel(
    sequenceId,
    interactionId,
    phaseState,
    choices,
    userHasCompletedPhase2,
    secondAttemptAllowed,
    secondAttemptAlreadySubmitted,
    responseFormModel
)