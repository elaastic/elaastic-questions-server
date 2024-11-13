package org.elaastic.player.steps

import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionType

object StepsModelFactory {

    fun buildForTeacher(sequence: Sequence) = StepsModel(
        responseSubmissionState =
        sequence.interactions[InteractionType.ResponseSubmission]?.stateForRegisteredUsers()
            .let(::interactionStateToPhaseState),
        evaluationState =
        sequence.interactions[InteractionType.Evaluation]?.stateForRegisteredUsers()
            .let(::interactionStateToPhaseState),
        readState =
        sequence.interactions[InteractionType.Read]?.stateForRegisteredUsers().let(::interactionStateToPhaseState),
        showStatistics = true,
        phase2Skipped = sequence.phase2Skipped
    )

    fun buildForLearner(sequence: Sequence, learnerActiveInteraction: Interaction?) = StepsModel(
        responseSubmissionState =
        sequence.interactions[InteractionType.ResponseSubmission]?.stateForLearner(learnerActiveInteraction!!)
            .let(::interactionStateToPhaseState),
        evaluationState =
        sequence.interactions[InteractionType.Evaluation]?.stateForLearner(learnerActiveInteraction!!)
            .let(::interactionStateToPhaseState),
        readState =
        sequence.interactions[InteractionType.Read]?.stateForLearner(learnerActiveInteraction!!)
            .let(::interactionStateToPhaseState),
        phase2Skipped = sequence.phase2Skipped

    )

    private fun interactionStateToPhaseState(state: State?): StepsModel.PhaseState =
        when (state) {
            null -> StepsModel.PhaseState.DISABLED
            State.beforeStart -> StepsModel.PhaseState.DISABLED
            State.show -> StepsModel.PhaseState.ACTIVE
            State.afterStop -> StepsModel.PhaseState.COMPLETED
        }

}