package org.elaastic.questions.player.components.dashboard;

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.player.phase.LearnerPhaseType

/**
 * Model for the learners monitoring dashboard.
 *
 * @property executionContext Sequence execution context type
 * @property phase1State the state of phase 1
 * @property phase2State the state of phase 2
 * @property phase3State the state of phase 3
 * @property learners the learners' states on each phase
 */
class LearnersMonitoringModel(
    val executionContext: ExecutionContext,
    val phase1State: DashboardPhaseState,
    val phase2State: DashboardPhaseState,
    val phase3State: DashboardPhaseState,
    val learners: MutableList<LearnerMonitoringModel> = mutableListOf()
) {
    /**
     *
     */
    fun setLearners(newLearnersList: MutableList<LearnerMonitoringModel>) {
        learners.clear()

        val newLearnersListSorted: MutableList<LearnerMonitoringModel>
            = when (this.executionContext) {
                ExecutionContext.FaceToFace -> this.sortWithFaceToFaceBehavior(newLearnersList)
                else                        -> this.sortWithBlendedOrRemoteBehavior(newLearnersList)
            }

        learners.addAll(newLearnersListSorted)
    }

    /**
     * Sort the learner when the sequence is in FaceToFace execution context
     *
     * If the Phase 1 is active then we want the learner still writing their answer in the top.
     *
     * If the Phase 2 is active then we want the learner still evaluating at first and more first the learner who didn't answer the question.
     *
     * If the Phase 3 is active then we want the learner who didn't answer and evaluate at first.
     */
    private fun sortWithFaceToFaceBehavior(
        newLearnersList: MutableList<LearnerMonitoringModel>
    ): MutableList<LearnerMonitoringModel> {

        newLearnersList.sortBy { it.learnerName }

        if (this.phase1State == DashboardPhaseState.IN_PROGRESS) {
            newLearnersList.sortByDescending { it.getLevelByStateCell(LearnerMonitoringModel.StateCell.IN_PROGRESS) }
        }

        if (this.phase2State == DashboardPhaseState.IN_PROGRESS) {
            newLearnersList.sortByDescending { it.getLevelByStateCell(LearnerMonitoringModel.StateCell.IN_PROGRESS) }
            newLearnersList.sortByDescending { it.getLevelByStateCell(LearnerMonitoringModel.StateCell.NOT_TERMINATED) }
        }

        if (this.phase3State == DashboardPhaseState.IN_PROGRESS) {
            newLearnersList.sortByDescending { it.getStateCell(LearnerPhaseType.RESPONSE) == LearnerMonitoringModel.StateCell.NOT_TERMINATED }
            newLearnersList.sortByDescending { it.getLevelByStateCell(LearnerMonitoringModel.StateCell.NOT_TERMINATED) }
        }

        return newLearnersList
    }

    /**
     * Sort the learners when the sequence has Blended or Remote execution context.
     *
     * Sort the learners alphabetically and by their "In Progress..." states count.
     */
    private fun sortWithBlendedOrRemoteBehavior(
        newLearnersList: MutableList<LearnerMonitoringModel>
    ): MutableList<LearnerMonitoringModel> {
        // Sorted By name alphabetically
        newLearnersList.sortBy { it.learnerName }
        // Sort by number of state in progress descending
        newLearnersList.sortByDescending { it.getLevelByStateCell(LearnerMonitoringModel.StateCell.IN_PROGRESS) }

        return newLearnersList
    }
}

/**
 * Model for a learner's state on each phase.
 *
 * Represents a learner's state in each phase, a line in the table.
 *
 * @property learnerId the learner's id
 * @property learnerName the learner's name
 * @property learnerStateOnPhase1 the learner's state on phase 1
 * @property learnerStateOnPhase2 the learner's state on phase 2
 * @property learnerStateOnPhase3 the learner's state on phase 3
 */
class LearnerMonitoringModel(
    val learnerId: Long,
    val learnerName: String,
    val learnerStateOnPhase1: LearnerStateOnPhase,
    val learnerStateOnPhase2: LearnerStateOnPhase = LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED,
    val learnerStateOnPhase3: LearnerStateOnPhase = LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED,
    val learnersMonitoringModel: LearnersMonitoringModel) {

    /**
     * Return the StateCell from a given phase
     * @property phase
     */
    fun getStateCell(phase: LearnerPhaseType): StateCell {
        val phaseState: DashboardPhaseState = this.getPhaseStateByType(phase)

        return when (this.getLearnerPhaseStateByType(phase)) {
            LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED -> {
                if (phaseState == DashboardPhaseState.IN_PROGRESS) {
                    StateCell.IN_PROGRESS
                } else if (phaseState == DashboardPhaseState.NOT_STARTED) {
                    StateCell.LOCKED
                } else {
                    StateCell.NOT_TERMINATED
                }
            }
            LearnerStateOnPhase.ACTIVITY_TERMINATED -> StateCell.TERMINATED
            LearnerStateOnPhase.WAITING -> {
                if (phaseState == DashboardPhaseState.IN_PROGRESS) {
                    StateCell.IN_PROGRESS
                } else {
                    StateCell.LOCKED
                }
            }
        }
    }

    /**
     * Return the LearnerStateOnPhase from the type of the phase
     * @property phase The type of the phase
     * @see LearnerPhaseType
     */
    private fun getLearnerPhaseStateByType(phase: LearnerPhaseType): LearnerStateOnPhase {
        return when (phase) {
            LearnerPhaseType.RESPONSE   -> this.learnerStateOnPhase1
            LearnerPhaseType.EVALUATION -> this.learnerStateOnPhase2
            LearnerPhaseType.RESULT     -> this.learnerStateOnPhase3
        }
    }

    /**
     * Return the PhaseState from the type of the phase
     * @property phase The type of the phase
     * @see LearnerPhaseType
     */
    private fun getPhaseStateByType(phase: LearnerPhaseType): DashboardPhaseState {
        return when (phase) {
            LearnerPhaseType.RESPONSE   -> this.learnersMonitoringModel.phase1State
            LearnerPhaseType.EVALUATION -> this.learnersMonitoringModel.phase2State
            LearnerPhaseType.RESULT     -> this.learnersMonitoringModel.phase3State
        }
    }

    /**
     * Return the number of state that is IN_PROGRESS
     */
    fun getLevelByStateCell(stateCell: StateCell): Int {
        val states: List<StateCell> = listOf(this.getStateCell(LearnerPhaseType.RESPONSE),
                                             this.getStateCell(LearnerPhaseType.EVALUATION),
                                             this.getStateCell(LearnerPhaseType.RESULT))

        return states.count { it == stateCell }
    }

    enum class StateCell {
        LOCKED,
        IN_PROGRESS,
        NOT_TERMINATED,
        TERMINATED
    }
}

/**
 * Enum defining states of a phase
 *
 * @property NOT_STARTED the phase has not started
 * @property IN_PROGRESS the phase is in progress
 * @property STOPPED the phase has been stopped
 * @property COMPLETED the phase has been completed
 */
enum class DashboardPhaseState {
    /**
     * The phase has not started
     *
     * Applicable to all phases.
     * During phase 3 (evaluation), the phase is not started when the results are not displayed yet
     */
    NOT_STARTED,

    /**
     * The phase is in progress
     *
     * Applicable to all phases.
     * During phase 3 (evaluation), the phase is in progress when the results are being displayed
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
    /**
     * The learner has not terminated the activity.
     */
    ACTIVITY_NOT_TERMINATED,

    /**
     * The learner has terminated the activity.
     */
    ACTIVITY_TERMINATED,

    /**
     * The learner is waiting for the next phase.
     */
    WAITING
}
