package org.elaastic.player.dashboard

import org.elaastic.sequence.ExecutionContext
import org.elaastic.sequence.phase.LearnerPhaseType
import org.elaastic.player.dashboard.DashboardPhaseState.IN_PROGRESS as Dashboard_State_IN_PROGRESS
import org.elaastic.player.dashboard.DashboardPhaseState.NOT_STARTED as Dashboard_State_NOT_STARTED
import org.elaastic.player.dashboard.DashboardPhaseState.STOPPED as Dashboard_State_STOPPED
import org.elaastic.player.dashboard.LearnerMonitoringModel.StateCell.IN_PROGRESS as Learner_State_IN_PROGRESS
import org.elaastic.player.dashboard.LearnerMonitoringModel.StateCell.NOT_TERMINATED as Learner_State_NOT_TERMINATED
import org.elaastic.player.dashboard.LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED as ACTIVITY_NOT_TERMINATED
import org.elaastic.sequence.phase.LearnerPhaseType.EVALUATION as Phase_EVALUATION
import org.elaastic.sequence.phase.LearnerPhaseType.RESPONSE as Phase_RESPONSE
import org.elaastic.sequence.phase.LearnerPhaseType.RESULT as Phase_RESULT

/**
 * Model for the learners monitoring dashboard.
 *
 * The model is used to display the state of the learners in the dashboard. It contains the following information:
 * - The execution context of the sequence
 * - The state of phase 1
 * - The state of phase 2
 * - The list of [LearnerMonitoringModel]
 *
 * @property executionContext Sequence execution context type
 * @property phase1State the state of phase 1
 * @property phase2State the state of phase 2
 * @property learners the learners' states on each phase
 * @property sequenceId the id of the sequence
 * @see DashboardPhaseState
 */
class SequenceMonitoringModel(
    val executionContext: ExecutionContext,
    val phase1State: DashboardPhaseState,
    val phase2State: DashboardPhaseState,
    val learners: MutableList<LearnerMonitoringModel> = mutableListOf(),
    val sequenceId: Long? = null
) {

    init {
        // Check coherence of the model
        when (executionContext) {
            ExecutionContext.FaceToFace -> {
                if (phase1State == Dashboard_State_IN_PROGRESS) require(phase2State == Dashboard_State_NOT_STARTED) { "In FaceToFace mode phase 2 must be not started when phase 1 is started" }
                if (phase2State == Dashboard_State_IN_PROGRESS) require(phase1State == Dashboard_State_STOPPED) { "In FaceToFace mode phase 1 must be completed when phase 2 is started" }
            }

            else -> require(phase1State == phase2State) { "In Remote mode phase 1 and 2 must have the same state" }
        }
    }

    fun isSequenceStarted(): Boolean {
        return phase1State != Dashboard_State_NOT_STARTED
    }

    /**
     * With the given list of learners, set the list of learners in the model.
     *
     * Replace the current list of learners with the new list. Before setting the new list, sort the learners according
     * to the execution context and the current phase.
     *
     * @param newLearnersList the new list of learners
     * @see ExecutionContext
     * @see sortWithFaceToFaceBehavior
     * @see sortWithBlendedOrRemoteBehavior
     */
    fun setLearners(newLearnersList: List<LearnerMonitoringModel>) {
        learners.clear()
        learners.addAll(
            when (this.executionContext) {
                ExecutionContext.FaceToFace -> this.sortWithFaceToFaceBehavior(newLearnersList)
                else -> this.sortWithBlendedOrRemoteBehavior(newLearnersList)
            }
        )
    }

    /**
     * Sort the learner when the sequence is in FaceToFace execution context
     *
     * If the Phase 1 is active, then we want the learner still writing their answer at the top.
     *
     * If the Phase 2 is active, then we want the learner still evaluating at first and more than the learner who didn't
     * answer the question.
     *
     * If the Phase 3 is active, then we want the learner who didn't answer and evaluate at first.
     */
    private fun sortWithFaceToFaceBehavior(
        newLearnersList: List<LearnerMonitoringModel>
    ): List<LearnerMonitoringModel> {
        val comparator = when {
            this.phase1State == Dashboard_State_IN_PROGRESS -> {
                compareByDescending {
                    it.getLevelByStateCell(Learner_State_IN_PROGRESS)
                }
            }

            this.phase2State == Dashboard_State_IN_PROGRESS -> {
                compareByDescending<LearnerMonitoringModel> {
                    it.getStateCell(Phase_EVALUATION) == Learner_State_IN_PROGRESS
                }.thenByDescending { it.getLevelByStateCell(Learner_State_NOT_TERMINATED) }
                    .thenByDescending { it.getLevelByStateCell(Learner_State_IN_PROGRESS) }
            }

            else -> {
                compareByDescending<LearnerMonitoringModel> {
                    it.getStateCell(Phase_RESPONSE) == Learner_State_NOT_TERMINATED
                }.thenByDescending { it.getLevelByStateCell(Learner_State_NOT_TERMINATED) }
            }
        }.thenBy { it.learnerName }

        return newLearnersList.sortedWith(comparator)
    }

    /**
     * Sort the learners when the sequence has Blended or Remote execution context.
     *
     * Sort the learners alphabetically and by their "In Progress..." states count.
     *
     * @param newLearnersList the list of learners to sort
     */
    private fun sortWithBlendedOrRemoteBehavior(
        newLearnersList: List<LearnerMonitoringModel>
    ): List<LearnerMonitoringModel> {
        return newLearnersList.sortedWith(
            compareByDescending<LearnerMonitoringModel> {
                it.getLevelByStateCell(Learner_State_IN_PROGRESS)
            }.thenBy {
                it.learnerName
            }
        )
    }
}

/**
 * Model for a learner's state on each phase.
 *
 * Represents a learner's state in each phase, a line in the table. The model contains the following information:
 * - The id of the learner
 * - The name of the learner
 * - The [LearnerStateOnPhase] of the learner on phase 1
 * - The [LearnerStateOnPhase] of the learner on phase 2
 *
 * @property userId the user id of the learner
 * @property learnerName the learner's name
 * @property learnerStateOnPhase1 the learner's state on phase 1
 * @property learnerStateOnPhase2 the learner's state on phase 2
 * @property learnerStateOnPhase3 the learner's state on phase 3
 */
class LearnerMonitoringModel(
    val userId: Long,
    val learnerName: String,
    val learnerStateOnPhase1: LearnerStateOnPhase,
    val learnerStateOnPhase2: LearnerStateOnPhase = ACTIVITY_NOT_TERMINATED,
    private val learnerStateOnPhase3: LearnerStateOnPhase = ACTIVITY_NOT_TERMINATED,
    val sequenceMonitoringModel: SequenceMonitoringModel
) {

    /**
     * Return the StateCell from a given phase type and the learner's state
     *
     * @param phase the type of the phase
     * @return the StateCell of the phase
     * @see StateCell
     * @see LearnerPhaseType
     * @see LearnerStateOnPhase
     */
    fun getStateCell(phase: LearnerPhaseType): StateCell {
        val phaseState: DashboardPhaseState = this.getPhaseStateByType(phase)

        return when (this.getLearnerPhaseStateByType(phase)) {
            ACTIVITY_NOT_TERMINATED -> {
                when (phaseState) {
                    Dashboard_State_IN_PROGRESS -> {
                        Learner_State_IN_PROGRESS
                    }

                    Dashboard_State_NOT_STARTED -> {
                        StateCell.LOCKED
                    }

                    else -> {
                        Learner_State_NOT_TERMINATED
                    }
                }
            }

            LearnerStateOnPhase.ACTIVITY_TERMINATED -> StateCell.TERMINATED
            LearnerStateOnPhase.WAITING -> {
                if (phaseState == Dashboard_State_IN_PROGRESS) {
                    Learner_State_IN_PROGRESS
                } else {
                    StateCell.LOCKED
                }
            }
        }
    }

    /**
     * Return the StateCell of the learner in the response phase
     *
     * Function used in the thymeleaf template
     *
     * @return the StateCell of the learner in the response phase
     * @see StateCell
     */
    fun getStateCellInResponsePhase(): StateCell {
        return getStateCell(Phase_RESPONSE)
    }

    /**
     * Return the StateCell of the learner in the evaluation phase
     *
     * Function used in the thymeleaf template
     *
     * @return the StateCell of the learner in the evaluation phase
     * @see StateCell
     */
    fun getStateCellInEvaluationPhase(): StateCell {
        return getStateCell(Phase_EVALUATION)
    }

    /**
     * Return the LearnerStateOnPhase from the type of the phase
     *
     * @param phase The type of the phase
     * @see LearnerPhaseType
     */
    private fun getLearnerPhaseStateByType(phase: LearnerPhaseType): LearnerStateOnPhase {
        return when (phase) {
            Phase_RESPONSE -> this.learnerStateOnPhase1
            Phase_EVALUATION -> this.learnerStateOnPhase2
            Phase_RESULT -> this.learnerStateOnPhase3
        }
    }

    /**
     * Return the PhaseState from the type of the phase
     *
     * @param phase The type of the phase
     * @see LearnerPhaseType
     */
    private fun getPhaseStateByType(phase: LearnerPhaseType): DashboardPhaseState {
        return when (phase) {
            Phase_RESPONSE -> this.sequenceMonitoringModel.phase1State
            else -> this.sequenceMonitoringModel.phase2State
        }
    }

    /**
     * @param stateCell the state of the cell we want to count
     * @return the number of states that is given
     * @see StateCell
     */
    fun getLevelByStateCell(stateCell: StateCell): Int {
        val states: List<StateCell> = listOf(
            this.getStateCell(Phase_RESPONSE),
            this.getStateCell(Phase_EVALUATION),
        )

        return states.count { it == stateCell }
    }

    /**
     * Enum defining states of a cell for a learner
     *
     * @property LOCKED the learner can't access the phase
     * @property Learner_State_IN_PROGRESS the learner is currently working on the phase
     * @property Learner_State_NOT_TERMINATED the learner has not terminated the phase
     * @property TERMINATED the learner has terminated the phase
     */
    enum class StateCell {
        LOCKED,
        IN_PROGRESS,
        NOT_TERMINATED,
        TERMINATED
    }

    fun hasAnswered(): Boolean {
        return this.learnerStateOnPhase1 == LearnerStateOnPhase.ACTIVITY_TERMINATED
    }
}

/**
 * Enum defining states of a phase
 *
 * @property Dashboard_State_NOT_STARTED the phase has not started
 * @property Dashboard_State_IN_PROGRESS the phase is in progress
 * @property Dashboard_State_STOPPED the phase has been stopped
 * @property COMPLETED the phase has been completed
 */
enum class DashboardPhaseState {
    /**
     * The phase has not started
     *
     * Applicable to all phases. During phase 3 (evaluation), the phase is not started when the results are not
     * displayed yet
     */
    NOT_STARTED,

    /**
     * The phase is in progress
     *
     * Applicable to all phases. During phase 3 (evaluation), the phase is in progress when the results are being
     * displayed
     */
    IN_PROGRESS,

    /**
     * The phase has been stopped
     *
     * Only applicable to phase 1 and 2.
     */
    STOPPED,                                // UNUSED

    /**
     * The phase has been completed
     *
     * Applicable to all phases.
     */
    COMPLETED
}

/**
 * Enum defining states of a learner on a phase
 *
 * @property ACTIVITY_NOT_TERMINATED the learner has not terminated the activity
 * @property ACTIVITY_TERMINATED the learner has terminated the activity
 * @property WAITING the learner is waiting for the next phase
 */
enum class LearnerStateOnPhase {
    /** The learner has not terminated the activity. */
    ACTIVITY_NOT_TERMINATED,

    /** The learner has terminated the activity. */
    ACTIVITY_TERMINATED,

    /** The learner is waiting for the next phase. */
    WAITING
}
