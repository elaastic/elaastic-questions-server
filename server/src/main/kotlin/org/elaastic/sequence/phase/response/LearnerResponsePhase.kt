/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.sequence.phase.response

import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.State
import org.elaastic.sequence.config.ResponseSubmissionSpecification
import org.elaastic.sequence.phase.*

class LearnerResponsePhase(
    learnerSequence: ILearnerSequence,
    index: Int,
    active: Boolean,
    state: State,
) : LearnerPhase(
    learnerSequence,
    index,
    active,
    state,
    PhaseTemplate(
        "${PhaseTemplate.TEMPLATE_PACKAGE}/response/_response-phase.html",
        "responsePhase"
    )
) {

    override val phaseType = LearnerPhaseType.RESPONSE
    override var learnerPhaseExecution: LearnerResponsePhaseExecution? = null

    override fun loadPhaseExecution(learnerPhaseExecution: LearnerPhaseExecution) {
        if (learnerPhaseExecution is LearnerResponsePhaseExecution)
            this.learnerPhaseExecution = learnerPhaseExecution
        else throw IllegalArgumentException()
    }

    override fun getViewModel(): PhaseViewModel = run {
        // TODO we should get rid of interaction here
        val interaction = learnerSequence.sequence.getResponseSubmissionInteraction()

        LearnerResponsePhaseViewModel(
            sequenceId = learnerSequence.sequence.id ?: error("Sequence must have an ID to get a response"),
            interactionId = interaction.id ?: error("Interaction must have an ID to get response"),
            learnerPhaseState = state,
            responseSubmitted = learnerPhaseExecution?.responseSubmitted ?: false,
            responseFormModel = LearnerResponseFormViewModel(
                interactionId = interaction.id ?: error("Interaction must have an ID to get response"),
                attempt = 1,
                responseSubmissionSpecification = interaction.specification as ResponseSubmissionSpecification,
                hasChoices = learnerSequence.sequence.statement.hasChoices(),
                multipleChoice = learnerSequence.sequence.statement.isMultipleChoice(),
                nbItem = learnerSequence.sequence.statement.choiceSpecification?.nbCandidateItem
            )
        )
    }
}