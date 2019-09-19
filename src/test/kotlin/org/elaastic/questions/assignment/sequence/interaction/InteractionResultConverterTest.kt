package org.elaastic.questions.assignment.sequence.interaction

import org.junit.jupiter.api.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.BeforeEach


internal class InteractionResultConverterTest {

    lateinit var interactionResultConverter: InteractionResultConverter

    @BeforeEach
    fun setUp() {
        interactionResultConverter = InteractionResultConverter()
    }


    @Test
    fun `result should be preserved by the conversion to database combined with the conversion from database`() {
        // Given
        val result = InteractionResult(
                ResultOfGroupOnAttempt(10, listOf(4, 5), 1),
                ResultOfGroupOnAttempt(10, listOf(6, 4), 0)
        )

        // Expect
        assertThat(
                result
                        .let { interactionResultConverter.convertToDatabaseColumn(it) }
                        .let { interactionResultConverter.convertToEntityAttribute(it) },
                equalTo(result)
        )
    }

}