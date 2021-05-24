package org.elaastic.questions.player.phase.evaluation.one_by_one

import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseViewModel
import org.elaastic.questions.player.phase.evaluation.ResponseData
import org.elaastic.questions.player.phase.response.LearnerResponseFormViewModel

data class OneByOneLearnerEvaluationPhaseViewModel(
    val sequenceId: Long,
    val interactionId: Long,
    val userActiveInteractionState: State,
    val choices: Boolean,
    val activeInteractionRank: Int,
    val userHasCompletedPhase2: Boolean,
    val nextResponseToGrade: ResponseData?,
    val secondAttemptAllowed: Boolean,
    val secondAttemptAlreadySubmitted: Boolean,
    val responseFormModel: LearnerResponseFormViewModel
)  : AbstractLearnerEvaluationPhaseViewModel()