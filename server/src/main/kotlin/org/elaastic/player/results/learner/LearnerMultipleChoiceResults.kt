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

package org.elaastic.player.results.learner

import org.elaastic.material.instructional.question.MultipleChoiceSpecification
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.material.instructional.question.legacy.LearnerChoice
import org.elaastic.player.explanations.ExplanationData

class LearnerMultipleChoiceResults(
    override val explanationFirstTry: ExplanationData?,
    override val explanationSecondTry: ExplanationData?,
    val choiceFirstTry: LearnerChoice?,
    val choiceSecondTry: LearnerChoice?,
    val scoreFirstTry: Int?,
    val scoreSecondTry: Int?,
    val expectedChoice: MultipleChoiceSpecification,
) : LearnerResultsModel {

    override fun getQuestionType() = QuestionType.MultipleChoice

    override fun hasAnsweredPhase1() = choiceFirstTry != null
    override fun hasAnsweredPhase2() = choiceSecondTry != null

    override fun areBothResponsesEqual(): Boolean = choiceFirstTry == choiceSecondTry

    override fun areBothExplanationsEqual(): Boolean = (explanationFirstTry?.content == explanationSecondTry?.content)

    fun isCorrectAnswer(itemId: Int): Boolean =
        expectedChoice.expectedChoiceList.any { choiceItem -> choiceItem.index == itemId }

    fun isFirstChoiceEmpty(): Boolean =
        choiceFirstTry == null

    fun isSecondChoiceEmpty(): Boolean =
        choiceSecondTry == null
}