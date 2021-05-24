package org.elaastic.questions.player.phase.evaluation.one_by_one

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.LearnerPhase
import org.elaastic.questions.player.phase.LearnerPhaseFactory
import org.elaastic.questions.player.phase.descriptor.PhaseConfig

class OneByOneLearnerEvaluationPhaseFactory : LearnerPhaseFactory {
    override fun build(
        learnerSequence: ILearnerSequence,
        phaseIndex: Int,
        active: Boolean,
        state: State,
        phaseConfig: PhaseConfig?
    ): LearnerPhase =
        OneByOneLearnerEvaluationPhase(
            learnerSequence,
            phaseIndex,
            active,
            state,
        )
}