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

package org.elaastic.sequence.interaction.response

import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.sequence.interaction.response.Response
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import javax.validation.Validation


class ResponseTest {

    @Test
    @DisplayName("Test compute score for multiple choice question: nominal case with 2 good choices among 5")
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
        assertEquals(BigDecimal(100), Response.computeScore(learnerChoiceAllGood, multipleChoiceSpecification))
        assertEquals(BigDecimal(0), Response.computeScore(learnerChoiceAllBad, multipleChoiceSpecification))
        assertEquals(BigDecimal(0), Response.computeScore(learnerChoiceMixNegative, multipleChoiceSpecification))
        assertEquals(BigDecimal(17), Response.computeScore(learnerChoiceMixPositive, multipleChoiceSpecification))
        assertEquals(BigDecimal(0), Response.computeScore(learnerChoiceAllSelected, multipleChoiceSpecification))
    }

    @Test
    @DisplayName("Test compute score for multiple choice question: limit case with only good choices among 3")
    fun testResponseComputeScoreMultipleChoiceQuestionOnlyGoodChoices() {
        // Given a multiple choice question
        val multipleChoiceSpecification = MultipleChoiceSpecification(3, listOf(
            ChoiceItem(1, 33.33f),
            ChoiceItem(2, 33.33f),
            ChoiceItem(3, 33.33f)),
            )
        // and a list of choices from learner
        val learnerChoiceAllGood = LearnerChoice(listOf(1, 2, 3))
        val learnerChoiceMixPositive = LearnerChoice(listOf(1, 2))
        // Expected correct comput of scores
        assertEquals(BigDecimal(100), Response.computeScore(learnerChoiceAllGood, multipleChoiceSpecification))
        assertEquals(BigDecimal(67), Response.computeScore(learnerChoiceMixPositive, multipleChoiceSpecification))
    }

    @Test
    @DisplayName("Test validation fails for multiple choice question: limit case with 0 good choices among 3")
    fun testValidationMultipleChoiceQuestionNoGoodChoices() {
        // Given a multiple choice question
        val multipleChoiceSpecification = MultipleChoiceSpecification(3, listOf())
        // and a javax validator
        val validator = Validation.buildDefaultValidatorFactory().validator
        // when the validator validate the multiple choice specification
        val constraintViolations = validator.validate(multipleChoiceSpecification)
        // Then the validation fails
        assertEquals(1, constraintViolations.size)
    }

    @Test
    @DisplayName("Test compute score for exclusive choice question: nominal case with 1 good choice among 3")
    fun testResponseComputeScoreExclusiveChoiceQuestion() {
        // Given a exclusive choice question
        val exclusiveChoiceSpecification = ExclusiveChoiceSpecification(3, ChoiceItem(2, 100f))
        // and a list of choices from learner
        val learnerChoiceGood = LearnerChoice(listOf(2))
        val learnerChoiceBad = LearnerChoice(listOf(1))
        val learnerChoiceEmpty = LearnerChoice(listOf())
        // Expected correct comput of scores
        assertEquals(BigDecimal(100), Response.computeScore(learnerChoiceGood, exclusiveChoiceSpecification))
        assertEquals(BigDecimal(0), Response.computeScore(learnerChoiceBad, exclusiveChoiceSpecification))
        assertEquals(BigDecimal(0), Response.computeScore(learnerChoiceEmpty, exclusiveChoiceSpecification))
    }
}
