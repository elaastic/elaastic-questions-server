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

import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.player.explanations.ExplanationData

class LearnerOpenResults(
    override val explanationFirstTry: ExplanationData?,
    override val explanationSecondTry: ExplanationData?,
) : LearnerResultsModel {

    override fun getQuestionType() = QuestionType.OpenEnded

    override fun hasAnsweredPhase1() = explanationFirstTry != null
    override fun hasAnsweredPhase2() = explanationSecondTry != null

    override fun areBothResponsesEqual(): Boolean = explanationFirstTry?.content == explanationSecondTry?.content

    override fun areBothExplanationsEqual(): Boolean = areBothResponsesEqual()

}