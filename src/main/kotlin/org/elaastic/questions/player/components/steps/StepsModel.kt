package org.elaastic.questions.player.components.steps

data class StepsModel(
        val responseSubmissionState: PhaseState,
        val evaluationState: PhaseState,
        val readState: PhaseState,
        val showStatistics: Boolean = false,
        val studentsProvideExplanation: Boolean = true
) {
    enum class PhaseState { DISABLED, ACTIVE, COMPLETED }
}