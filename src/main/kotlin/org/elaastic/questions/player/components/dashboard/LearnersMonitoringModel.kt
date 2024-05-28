package org.elaastic.questions.player.components.dashboard;

import org.elaastic.questions.player.phase.LearnerPhaseType

/**
 * Model for the learners monitoring dashboard.
 *
 * @property phase1State the state of phase 1
 * @property phase2State the state of phase 2
 * @property phase3State the state of phase 3
 * @property learners the learners' states on each phase
 */
class LearnersMonitoringModel(
    val phase1State: PhaseState,
    val phase2State: PhaseState,
    val phase3State: PhaseState,
    val learners: MutableList<LearnerMonitoringModel>
)

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
    val learnerStateOnPhase2: LearnerStateOnPhase,
    val learnerStateOnPhase3: LearnerStateOnPhase,
    val learnersMonitoringModel: LearnersMonitoringModel) {

    /**
     * Return the StateCell from a given phase
     * @property phase
     */
    fun getStateCell(phase: LearnerPhaseType): StateCell
        = when (this.getLearnerPhaseStateByType(phase)) {
            LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED -> {
                if (this.getPhaseStateByType(phase) == PhaseState.IN_PROGRESS) {
                    StateCell.IN_PROGRESS
                } else {
                    StateCell.NOT_TERMINATED
                }
            }
            LearnerStateOnPhase.ACTIVITY_TERMINATED -> {
                if (this.getPhaseStateByType(phase) == PhaseState.COMPLETED) {
                    StateCell.TERMINATED
                } else {
                    StateCell.NOT_TERMINATED
                }
            }
            LearnerStateOnPhase.WAITING -> StateCell.LOCKED
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
    private fun getPhaseStateByType(phase: LearnerPhaseType): PhaseState {
        return when (phase) {
            LearnerPhaseType.RESPONSE   -> this.learnersMonitoringModel.phase1State
            LearnerPhaseType.EVALUATION -> this.learnersMonitoringModel.phase2State
            LearnerPhaseType.RESULT     -> this.learnersMonitoringModel.phase3State
        }
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
enum class PhaseState {
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
    STOPPED,

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
