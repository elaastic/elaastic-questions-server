package org.elaastic.player.steps

data class StepsModel(
        val responseSubmissionState: PhaseState,
        val evaluationState: PhaseState,
        val readState: PhaseState,
        val showStatistics: Boolean = false,
        val studentsProvideExplanation: Boolean = true,
        val phase2Skipped: Boolean = false
) {
    enum class PhaseState { DISABLED, ACTIVE, COMPLETED }
}