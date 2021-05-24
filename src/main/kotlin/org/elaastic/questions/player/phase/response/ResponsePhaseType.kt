package org.elaastic.questions.player.phase.response

import org.elaastic.questions.player.phase.PhaseType

object ResponsePhaseType : PhaseType(
    LearnerResponsePhaseFactory(),
    "learnerResponsePhaseExecutionService"
)