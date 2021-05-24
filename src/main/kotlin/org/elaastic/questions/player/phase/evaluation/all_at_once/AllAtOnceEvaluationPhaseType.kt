package org.elaastic.questions.player.phase.evaluation.all_at_once

import org.elaastic.questions.player.phase.PhaseType

object AllAtOnceEvaluationPhaseType : PhaseType(
    AllAtOnceLearnerEvaluationPhaseFactory(),
    "allAtOnceLearnerEvaluationPhaseExecutionService"
)