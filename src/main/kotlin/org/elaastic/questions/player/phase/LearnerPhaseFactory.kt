package org.elaastic.questions.player.phase

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.descriptor.PhaseConfig

interface LearnerPhaseFactory {

    fun build(
        learnerSequence: ILearnerSequence,
        phaseIndex: Int,
        active: Boolean,
        state: State,
        phaseConfig: PhaseConfig? = null
    ): LearnerPhase
}