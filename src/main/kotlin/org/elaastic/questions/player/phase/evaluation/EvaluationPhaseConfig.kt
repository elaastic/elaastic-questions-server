package org.elaastic.questions.player.phase.evaluation

import org.elaastic.questions.player.phase.descriptor.PhaseConfig

enum class EvaluationPhaseConfig: PhaseConfig {
    // Implementation that shows all the alternative peer answers to evaluate
    ALL_AT_ONCE,

    // ONE_BY_ONE with DRAXO Form evaluation
    DRAXO;
}