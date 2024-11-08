package org.elaastic.sequence.phase.evaluation

import org.elaastic.sequence.State
import org.elaastic.sequence.phase.PhaseViewModel
import org.elaastic.sequence.phase.response.LearnerResponseFormViewModel

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