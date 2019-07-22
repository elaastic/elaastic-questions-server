package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.sequence.TeacherExplanation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

/**
 * @author John Tranier
 */

class TeacherExplanationTest {

    lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `the explanation must not be blank`() {
        // Given:
        val emptyExplanation = TeacherExplanation(1, "")

        // Then:
        assertEquals(1, validator.validate(emptyExplanation).size)

        // Given:
        val correctExplanation = TeacherExplanation(2, "correct")

        // Then:
        assertEquals(0, validator.validate(correctExplanation).size)
    }
}