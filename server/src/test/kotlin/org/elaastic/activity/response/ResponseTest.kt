package org.elaastic.activity.response

import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.junit.jupiter.api.Assertions
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
        Assertions.assertEquals(
            BigDecimal(100),
            Response.computeScore(learnerChoiceAllGood, multipleChoiceSpecification)
        )
        Assertions.assertEquals(BigDecimal(0), Response.computeScore(learnerChoiceAllBad, multipleChoiceSpecification))
        Assertions.assertEquals(
            BigDecimal(0),
            Response.computeScore(learnerChoiceMixNegative, multipleChoiceSpecification)
        )
        Assertions.assertEquals(
            BigDecimal(17),
            Response.computeScore(learnerChoiceMixPositive, multipleChoiceSpecification)
        )
        Assertions.assertEquals(
            BigDecimal(0),
            Response.computeScore(learnerChoiceAllSelected, multipleChoiceSpecification)
        )
    }

    @Test
    @DisplayName("Test compute score for multiple choice question: limit case with only good choices among 3")
    fun testResponseComputeScoreMultipleChoiceQuestionOnlyGoodChoices() {
        // Given a multiple choice question
        val multipleChoiceSpecification = MultipleChoiceSpecification(
            3,
            listOf(
                ChoiceItem(1, 33.33f),
                ChoiceItem(2, 33.33f),
                ChoiceItem(3, 33.33f)
            ),
        )
        // and a list of choices from learner
        val learnerChoiceAllGood = LearnerChoice(listOf(1, 2, 3))
        val learnerChoiceMixPositive = LearnerChoice(listOf(1, 2))
        // Expected correct comput of scores
        Assertions.assertEquals(
            BigDecimal(100),
            Response.computeScore(learnerChoiceAllGood, multipleChoiceSpecification)
        )
        Assertions.assertEquals(
            BigDecimal(67),
            Response.computeScore(learnerChoiceMixPositive, multipleChoiceSpecification)
        )
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
        Assertions.assertEquals(1, constraintViolations.size)
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
        Assertions.assertEquals(BigDecimal(100), Response.computeScore(learnerChoiceGood, exclusiveChoiceSpecification))
        Assertions.assertEquals(BigDecimal(0), Response.computeScore(learnerChoiceBad, exclusiveChoiceSpecification))
        Assertions.assertEquals(BigDecimal(0), Response.computeScore(learnerChoiceEmpty, exclusiveChoiceSpecification))
    }
}