package org.elaastic.player.steps

import org.elaastic.player.steps.StepsModel.PhaseState
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.Interaction

object StepsModelFactory {

    fun buildForTeacher(sequence: Sequence) = StepsModel(
        responseSubmission = sequence
            .getResponseSubmissionInteractionOrNull()
            ?.let { PhaseStep(it.stateForRegisteredUsers().toPhaseState(), it.rank) },
        evaluation = sequence
            .getEvaluationInteractionOrNull()
            ?.let { PhaseStep(it.stateForRegisteredUsers().toPhaseState(), it.rank) },
        read = sequence
            .getReadInteractionOrNull()
            ?.let { PhaseStep(it.stateForRegisteredUsers().toPhaseState(), it.rank) },
        showStatistics = true,
        phase2Skipped = sequence.phase2Skipped
    )

    fun buildForLearner(sequence: Sequence, learnerActiveInteraction: Interaction?) = StepsModel(
        responseSubmission = sequence
            .getResponseSubmissionInteractionOrNull()
            ?.let { PhaseStep(it.stateForLearner(learnerActiveInteraction!!).toPhaseState(), it.rank) },
        evaluation = sequence
            .getEvaluationInteractionOrNull()
            ?.let { PhaseStep(it.stateForLearner(learnerActiveInteraction!!).toPhaseState(), it.rank) },
        read = sequence
            .getReadInteractionOrNull()
            ?.let { PhaseStep(it.stateForLearner(learnerActiveInteraction!!).toPhaseState(), it.rank) },
        phase2Skipped = sequence.phase2Skipped
    )

    private fun State?.toPhaseState(): PhaseState =
        when (this) {
            null -> PhaseState.NONE
            State.beforeStart -> PhaseState.DISABLED
            State.show -> PhaseState.ACTIVE
            State.afterStop -> PhaseState.COMPLETED
        }

}