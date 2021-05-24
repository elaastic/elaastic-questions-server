package org.elaastic.questions.player.phase.result

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.LearnerPhase
import org.elaastic.questions.player.phase.LearnerPhaseFactory
import org.elaastic.questions.player.phase.descriptor.PhaseConfig
import org.elaastic.questions.player.phase.response.LearnerResponsePhase

class LearnerResultPhaseFactory : LearnerPhaseFactory {

    override fun build(
        learnerSequence: ILearnerSequence,
        phaseIndex: Int,
        active: Boolean,
        state: State,
        phaseConfig: PhaseConfig?
    ): LearnerPhase =
        LearnerResponsePhase(
            learnerSequence,
            phaseIndex,
            active,
            state,
        )
}