package org.elaastic.sequence.phase

import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.State

/**
 * The phase of a sequence for a learner.
 * A phase has a type (RESPONSE, EVALUATION, RESULT) ; various implementation may be proposed for a type.
 * A phase has a specific position into its sequence denoted by 'index' (I think this info should be only hold by the sequence)
 * The active phase in a sequence is the one that the learner can fulfill (Note: this info should also by hold by the learner sequence).
 *
 * When it is active, a phase will be rendered into the player for a learner.
 * To do so, it has
 *  - a PhaseTemplate (its thymeleaf template)
 *  - a PhaseViewModel (the model that will be provided to the template)
 *  - a PhaseController that will expose the actions available to the learner (through the template)
 *  - a LearnerPhaseExecution : the data required to execute the phase (that will be loaded only when needed)
 *  - a LearnerPhaseExecutionLoader : the service that will interact with other application services to retrieve the LearnerPhaseExecution
 */
abstract class LearnerPhase(
    val learnerSequence: ILearnerSequence,
    val index: Int,
    val active: Boolean,
    val state: State,
    val playerTemplate: PhaseTemplate,
) {

    abstract val phaseType: LearnerPhaseType
    abstract val learnerPhaseExecution: LearnerPhaseExecution?

    fun getLearnerPhaseExecutionLoaderName(): String =
        this::class.java.simpleName + "ExecutionLoader"

    abstract fun loadPhaseExecution(learnerPhaseExecution: LearnerPhaseExecution)

    abstract fun getViewModel(): PhaseViewModel

    open fun isVisible() =
        learnerSequence.isInProgress() && this.active

    fun isActive(): Boolean = this.active

    fun isInProgress() = state == State.show
}