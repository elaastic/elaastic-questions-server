package org.elaastic.questions.player.phase

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State

abstract class LearnerPhase(
    val learnerSequence: ILearnerSequence,
    val index: Int,
    val active: Boolean,
    val state: State,
    val playerTemplate: PhaseTemplate,
) {

    abstract val phaseType: PhaseType
    abstract val learnerPhaseExecution: LearnerPhaseExecution?

    abstract fun loadPhaseExecution(learnerPhaseExecution: LearnerPhaseExecution)

    fun isVisible() =
        learnerSequence.isInProgress() && this.active

    fun isActive(): Boolean = this.active

    fun isInProgress() = state == State.show
}