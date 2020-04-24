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
package org.elaastic.questions.player.components.responsePhase

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.results.AttemptNum
import org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification

object ResponsePhaseModelFactory {

    fun build(responseSubmitted: Boolean,
              sequence: Sequence,
              userActiveInteraction: Interaction?) = run {

        val interaction = sequence.getResponseSubmissionInteraction()

        ResponsePhaseModel(
                sequenceId = sequence.id ?: error("Sequence must have an ID to get a response"),
                interactionId = interaction.id ?: error("Interaction must have an ID to get response"),
                userActiveInteractionState = userActiveInteraction?.state ?: State.beforeStart,
                responseSubmitted = responseSubmitted,
                responseFormModel = ResponseFormModel(
                        interactionId = interaction.id ?: error("Interaction must have an ID to get response"),
                        attempt = 1,
                        responseSubmissionSpecification = interaction.specification as ResponseSubmissionSpecification,
                        timeToProvideExplanation = true,
                        hasChoices = sequence.statement.hasChoices(),
                        multipleChoice = sequence.statement.isMultipleChoice(),
                        nbItem = sequence.statement.choiceSpecification?.nbCandidateItem
                )
        )
    }
}