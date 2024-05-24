package org.elaastic.questions.player.components.dashboard;

class LearnersMonitoringModel(
    val phase1State: PhaseState,
    val phase2State: PhaseState,
    val phase3State: PhaseState,
    val learners: List<LearnerMonitoringModel>
)

// represents a learner's state on each phase, a line in the table
class LearnerMonitoringModel(
    val learnerId: Long,
    val learnerName: String,
    val learnerStateOnPhase1: LearnerStateOnPhase,
    val learnerStateOnPhase2: LearnerStateOnPhase,
    val learnerStateOnPhase3: LearnerStateOnPhase
)

// enum defining states of a phase
enum class PhaseState {
    NOT_STARTED,
    IN_PROGRESS,
    STOPPED,
    COMPLETED
}

enum class LearnerStateOnPhase {
    ACTIVITY_NOT_TERMINATED,
    ACTIVITY_TERMINATED,
    WAITING
}
