package org.elaastic.questions.player.phase.evaluation.draxo

import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseViewModel
import org.elaastic.questions.player.phase.evaluation.ResponseData
import org.elaastic.questions.player.phase.response.LearnerResponseFormViewModel

class DraxoLearnerEvaluationPhaseViewModel(
    sequenceId: Long,
    interactionId: Long,
    phaseState: State,
    choices: Boolean,
    userHasCompletedPhase2: Boolean,
    val nextResponseToGrade: ResponseData?,
    val lastResponseToGrade: Boolean,
    secondAttemptAllowed: Boolean,
    secondAttemptAlreadySubmitted: Boolean,
    responseFormModel: LearnerResponseFormViewModel,
    val draxoEvaluation: DraxoEvaluation = DraxoEvaluation(),
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