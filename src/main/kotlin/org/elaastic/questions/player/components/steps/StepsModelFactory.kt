package org.elaastic.questions.player.components.steps

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.InteractionType

object StepsModelFactory {

    // TODO Take sequence state into account
    fun build(sequence: Sequence): StepsModel = StepsModel(
            responseSubmissionState =
            sequence.interactions[InteractionType.ResponseSubmission]?.stateForRegisteredUsers().let(::interactionStateToPhaseState),
            evaluationState =
            sequence.interactions[InteractionType.Evaluation]?.stateForRegisteredUsers().let(::interactionStateToPhaseState),
            readState =
            sequence.interactions[InteractionType.Read]?.stateForRegisteredUsers().let(::interactionStateToPhaseState)
    )

    private fun interactionStateToPhaseState(state: State?): StepsModel.PhaseState =
            when(state) {
                null -> StepsModel.PhaseState.DISABLED
                State.beforeStart -> StepsModel.PhaseState.DISABLED
                State.show -> StepsModel.PhaseState.ACTIVE
                State.afterStop -> StepsModel.PhaseState.COMPLETED
            }

}