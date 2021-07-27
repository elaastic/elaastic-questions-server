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

package org.elaastic.questions.assignment.sequence.interaction.response

import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal


class ResponseTest {

    @Test
    fun testResponseComputeScoreMultipleChoiceQuestion() {
        // Given a multiple choice question
        val multipleChoiceSpecification = MultipleChoiceSpecification(5, listOf(ChoiceItem(1, 50f), ChoiceItem(3, 50f)))
        // and a list of choices from learner
        val learnerChoiceAllGood = LearnerChoice(listOf(1, 3))
        val learnerChoiceAllBad = LearnerChoice(listOf(2, 4, 5))
        val learnerChoiceMixPositive = LearnerChoice(listOf(1, 2))
        val learnerChoiceMixNegative = LearnerChoice(listOf(1, 2, 4))
        val learnerChoiceAllSelected = LearnerChoice(listOf(1, 2, 3, 4, 5))
        // Expected correct comput of scores
        assertEquals(BigDecimal(100),Response.computeScore(learnerChoiceAllGood, multipleChoiceSpecification))
        assertEquals(BigDecimal(0),Response.computeScore(learnerChoiceAllBad, multipleChoiceSpecification))
        assertEquals(BigDecimal(0),Response.computeScore(learnerChoiceMixNegative, multipleChoiceSpecification))
        assertEquals(BigDecimal(17),Response.computeScore(learnerChoiceMixPositive, multipleChoiceSpecification))
        assertEquals(BigDecimal(0),Response.computeScore(learnerChoiceAllSelected, multipleChoiceSpecification))
    }
}
