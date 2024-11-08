package org.elaastic.material.instructional.question

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

internal class ChoiceItemSpecificationTest {

    lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }


    @Test
    fun `check that the score cannot overcome 100`() {
        // Given:
        var choiceItemSpecification = ChoiceItem(
            1,
            723f
        )


        // Then:
        Assertions.assertEquals(
            1,
            validator.validate(choiceItemSpecification).size
        )

        // Given:
        choiceItemSpecification = ChoiceItem(
            1,
            100f
        )


        // Then:
        Assertions.assertEquals(
            0,
            validator.validate(choiceItemSpecification).size
        )
    }

}