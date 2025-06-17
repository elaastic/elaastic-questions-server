package org.elaastic.player.steps

import org.elaastic.player.steps.StepsModel.PhaseState
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.Interaction

object StepsModelFactory {

    fun buildForTeacher(sequence: Sequence) = StepsModel(
        responseSubmissionState = sequence
            .getResponseSubmissionInteractionOrNull()
            ?.stateForRegisteredUsers()
            .toPhaseState(),
        evaluationState = sequence
            .getEvaluationInteractionOrNull()
            ?.stateForRegisteredUsers()
            .toPhaseState(),
        readState = sequence
            .getReadInteractionOrNull()
            ?.stateForRegisteredUsers()
            .toPhaseState(),
        showStatistics = true,
        phase2Skipped = sequence.phase2Skipped
    )

    fun buildForLearner(sequence: Sequence, learnerActiveInteraction: Interaction?) = StepsModel(
        responseSubmissionState = sequence
            .getResponseSubmissionInteractionOrNull()
            ?.stateForLearner(learnerActiveInteraction!!)
            .toPhaseState(),
        evaluationState = sequence
            .getEvaluationInteractionOrNull()
            ?.stateForLearner(learnerActiveInteraction!!)
            .toPhaseState(),
        readState = sequence
            .getReadInteractionOrNull()
            ?.stateForLearner(learnerActiveInteraction!!)
            .toPhaseState(),
        phase2Skipped = sequence.phase2Skipped
    )

    private fun State?.toPhaseState(): PhaseState =
        when (this) {
            null -> PhaseState.DISABLED
            State.beforeStart -> PhaseState.DISABLED
            State.show -> PhaseState.ACTIVE
            State.afterStop -> PhaseState.COMPLETED
        }

}