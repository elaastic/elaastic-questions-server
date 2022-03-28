/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.player.components.steps

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionType

object StepsModelFactory {

    fun buildForTeacher(sequence: Sequence) = StepsModel(
            responseSubmissionState =
            sequence.interactions[InteractionType.ResponseSubmission]?.stateForRegisteredUsers().let(::interactionStateToPhaseState),
            evaluationState =
            sequence.interactions[InteractionType.Evaluation]?.stateForRegisteredUsers().let(::interactionStateToPhaseState),
            readState =
            sequence.interactions[InteractionType.Read]?.stateForRegisteredUsers().let(::interactionStateToPhaseState),
            showStatistics = true,
            phase2Skipped = sequence.phase2Skipped
    )

    fun buildForLearner(sequence: Sequence, learnerActiveInteraction: Interaction?) = StepsModel(
            responseSubmissionState =
            sequence.interactions[InteractionType.ResponseSubmission]?.stateForLearner(learnerActiveInteraction!!).let(::interactionStateToPhaseState),
            evaluationState =
            sequence.interactions[InteractionType.Evaluation]?.stateForLearner(learnerActiveInteraction!!).let(::interactionStateToPhaseState),
            readState =
            sequence.interactions[InteractionType.Read]?.stateForLearner(learnerActiveInteraction!!).let(::interactionStateToPhaseState),
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
