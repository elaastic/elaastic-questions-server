package org.elaastic.questions.assignment.sequence.interaction

import org.junit.jupiter.api.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.BeforeEach


/**
 * @author John Tranier
 */
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
                OneAttemptResult(listOf(0.33f, 0.33f, 0.33f)),
                OneAttemptResult(listOf(0.5f, 0f, 0.5f))
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