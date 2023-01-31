package org.elaastic.questions.player.phase.evaluation

import org.elaastic.questions.player.phase.descriptor.PhaseConfig

enum class LearnerEvaluationPhaseConfig: PhaseConfig {
    // Implementation that shows all the alternative peer answers to evaluate
    ALL_AT_ONCE,

    // Implementation that shows one by one the alternative peer answers ; the learner may change its own answer after
    // each evaluation and, then, stop the process
    ONE_BY_ONE
}