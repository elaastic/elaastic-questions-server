package org.elaastic.questions.player.phase.evaluation.all_at_once

import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseViewModel
import org.elaastic.questions.player.phase.evaluation.ResponseData
import org.elaastic.questions.player.phase.response.LearnerResponseFormViewModel

class AllAtOnceLearnerEvaluationPhaseViewModel(
    sequenceId: Long,
    interactionId: Long,
    phaseState: State,
    choices: Boolean,
    userHasCompletedPhase2: Boolean,
    val userHasPerformedEvaluation: Boolean,
    val responsesToGrade: List<ResponseData>,
    secondAttemptAllowed: Boolean,
    secondAttemptAlreadySubmitted: Boolean,
    responseFormModel: LearnerResponseFormViewModel
) : AbstractLearnerEvaluationPhaseViewModel(
    sequenceId,
    interactionId,
    phaseState,
    choices,
    userHasCompletedPhase2,
    secondAttemptAllowed,
    secondAttemptAlreadySubmitted,
    responseFormModel
)