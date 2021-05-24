package org.elaastic.questions.player.phase.result

import org.elaastic.questions.player.phase.PhaseType

object ResultPhaseType : PhaseType(
    LearnerResultPhaseFactory(),
    "learnerResultPhaseExecutionService"
)