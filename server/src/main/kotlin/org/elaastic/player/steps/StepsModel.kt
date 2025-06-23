package org.elaastic.player.steps

import org.elaastic.player.dashboard.DashboardPhaseState
import org.elaastic.player.dashboard.SequenceMonitoringModel

data class StepsModel(
    val responseSubmission: PhaseStep?,
    val evaluation: PhaseStep?,
    val read: PhaseStep?,
    val showStatistics: Boolean = false,
    val studentsProvideExplanation: Boolean = true,
    val phase2Skipped: Boolean = false
) {
    val sequenceStarted = responseSubmission != null || evaluation != null || read != null

    enum class PhaseState {
        DISABLED,
        ACTIVE,
        COMPLETED,
        NONE;

        /**
         * Since The [StepsModel] use different state phase than the [SequenceMonitoringModel], we need to convert the
         * state.
         *
         * @return the converted state in DashboardPhaseState
         * @see DashboardPhaseState
         */
        fun getDashboardState(): DashboardPhaseState {
            return when (this) {
                DISABLED -> DashboardPhaseState.NOT_STARTED
                ACTIVE -> DashboardPhaseState.IN_PROGRESS
                COMPLETED -> DashboardPhaseState.STOPPED
                NONE -> DashboardPhaseState.NONE
            }
        }
    }
}

data class PhaseStep(
    val state: StepsModel.PhaseState,
    val rank: Int,
)