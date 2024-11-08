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
package org.elaastic.sequence.phase.response

import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.sequence.interaction.response.Response
import org.elaastic.activity.results.AttemptNum
import org.elaastic.sequence.config.ResponseSubmissionSpecification

data class LearnerResponseFormViewModel(
    val interactionId: Long,
    val attempt: AttemptNum,
    val hasChoices: Boolean,
    val multipleChoice: Boolean,
    val nbItem: Int? = null,
    val firstAttemptChoices: Array<Int> = arrayOf(),
    val firstAttemptExplanation: String? = null,
    val firstAttemptConfidenceDegree: ConfidenceDegree? = null,
    val responseSubmissionSpecification: ResponseSubmissionSpecification,
    val ConfidenceDegreeValues: Array<ConfidenceDegree> = ConfidenceDegree.values()
)

object LearnerResponseFormViewModelFactory {
        fun buildFor2ndAttempt(learnerSequence: ILearnerSequence, firstAttemptResponse: Response?) = run {

                // TODO we should get rid of interaction here...
                val interactionId = learnerSequence.sequence.getEvaluationInteraction().id
                        ?: error("Interaction must have an ID to an evaluation")

                val sequence = learnerSequence.sequence

                LearnerResponseFormViewModel(
                        interactionId = interactionId,
                        attempt = 2,
                        responseSubmissionSpecification = sequence.getResponseSubmissionSpecification(),
                        hasChoices = sequence.statement.hasChoices(),
                        multipleChoice = sequence.statement.isMultipleChoice(),
                        nbItem = sequence.statement.choiceSpecification?.nbCandidateItem,
                        firstAttemptExplanation = firstAttemptResponse?.explanation,
                        firstAttemptChoices = firstAttemptResponse?.learnerChoice?.toTypedArray() ?: arrayOf(),
                        firstAttemptConfidenceDegree = firstAttemptResponse?.confidenceDegree
                )
        }
}