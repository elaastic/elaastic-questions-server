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

package org.elaastic.consolidation.subject.question

import org.elaastic.consolidation.subject.question.attachment.PracticeAttachment
import org.elaastic.consolidation.subject.question.specification.ExclusiveChoiceQuestionSpecification
import org.elaastic.consolidation.subject.question.specification.MultipleChoiceQuestionSpecification
import org.elaastic.consolidation.subject.question.specification.OpenQuestionSpecification
import org.elaastic.material.instructional.question.ChoiceSpecification
import org.elaastic.material.instructional.question.ExclusiveChoiceSpecification
import org.elaastic.material.instructional.question.MultipleChoiceSpecification
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.sequence.Sequence

object PracticeQuestionFactory {

    fun buildQuestion(sequence: Sequence, explanations: List<PracticeLearnerExplanation>?) =
        PracticeQuestion(
            id = sequence.uuid,
            rank = sequence.rank,
            title = sequence.statement.title,
            content = sequence.statement.content,
            expectedExplanation = sequence.statement.expectedExplanation ?: "",
            specification = when (sequence.statement.questionType) {
                QuestionType.OpenEnded -> OpenQuestionSpecification()
                QuestionType.ExclusiveChoice -> parseExclusiveChoiceSpecification(sequence.statement.choiceSpecification)
                QuestionType.MultipleChoice -> parseMultipleChoiceSpecification(sequence.statement.choiceSpecification)
            },
            attachment = sequence.statement.attachment?.let(::PracticeAttachment),
            explanations = if (explanations != null) explanations else emptyList()
        )

    private fun parseMultipleChoiceSpecification(choiceSpecification: ChoiceSpecification?) =
        when (choiceSpecification) {
            is MultipleChoiceSpecification ->
                MultipleChoiceQuestionSpecification(
                    nbCandidateItem = choiceSpecification.nbCandidateItem,
                    expectedChoiceIndexList = choiceSpecification.expectedChoiceList.map { it.index }
                )

            else -> throw IllegalArgumentException("provided choice specification is not a multiple choice specification !")
        }

    private fun parseExclusiveChoiceSpecification(choiceSpecification: ChoiceSpecification?) =
        when (choiceSpecification) {
            is ExclusiveChoiceSpecification ->
                ExclusiveChoiceQuestionSpecification(
                    nbCandidateItem = choiceSpecification.nbCandidateItem,
                    expectedChoiceIndex = choiceSpecification.expectedChoice.index
                )

            else -> throw IllegalArgumentException("provided choiceSpecification is not an exclusive choice")
        }
}