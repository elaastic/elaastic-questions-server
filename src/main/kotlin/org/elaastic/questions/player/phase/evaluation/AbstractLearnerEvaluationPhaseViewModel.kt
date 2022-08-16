package org.elaastic.questions.player.phase.evaluation

import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.PhaseViewModel
import org.elaastic.questions.player.phase.response.LearnerResponseFormViewModel

abstract class AbstractLearnerEvaluationPhaseViewModel(
    val sequenceId: Long,
    val interactionId: Long,
    val phaseState: State,
    val choices: Boolean,
    val userHasCompletedPhase2: Boolean,
    val secondAttemptAllowed: Boolean,
    val secondAttemptAlreadySubmitted: Boolean,
    val responseFormModel: LearnerResponseFormViewModel
) : PhaseViewModel