package org.elaastic.questions.player.phase.evaluation.one_by_one

import org.elaastic.questions.player.phase.PhaseType

object OneByOneEvaluationPhaseType : PhaseType(
    OneByOneLearnerEvaluationPhaseFactory(),
    "oneByOneLearnerEvaluationPhaseExecutionService"
)