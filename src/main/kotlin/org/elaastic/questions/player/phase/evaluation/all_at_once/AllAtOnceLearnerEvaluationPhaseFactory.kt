package org.elaastic.questions.player.phase.evaluation.all_at_once

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.LearnerPhase
import org.elaastic.questions.player.phase.LearnerPhaseFactory
import org.elaastic.questions.player.phase.descriptor.PhaseConfig
import java.lang.IllegalArgumentException

class AllAtOnceLearnerEvaluationPhaseFactory : LearnerPhaseFactory {
    override fun build(
        learnerSequence: ILearnerSequence,
        phaseIndex: Int,
        active: Boolean,
        state: State,
        phaseConfig: PhaseConfig?
    ): LearnerPhase =
         AllAtOnceLearnerEvaluationPhase(
            learnerSequence,
            phaseIndex,
            active,
            state,
        )
}