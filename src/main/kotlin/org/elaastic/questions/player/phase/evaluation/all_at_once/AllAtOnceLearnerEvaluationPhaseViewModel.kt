package org.elaastic.questions.player.phase.evaluation.all_at_once

import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseViewModel
import org.elaastic.questions.player.phase.evaluation.ResponseData
import org.elaastic.questions.player.phase.response.LearnerResponseFormViewModel

data class AllAtOnceLearnerEvaluationPhaseViewModel(
    val sequenceId: Long,
    val interactionId: Long,
    val userActiveInteractionState: State,
    val choices: Boolean,
    val activeInteractionRank: Int,
    val userHasCompletedPhase2: Boolean,
    val userHasPerformedEvaluation: Boolean,
    val responsesToGrade: List<ResponseData>,
    val secondAttemptAllowed: Boolean,
    val secondAttemptAlreadySubmitted: Boolean,
    val responseFormModel: LearnerResponseFormViewModel
) : AbstractLearnerEvaluationPhaseViewModel()