package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.sequence.interaction.results.ResponsesDistribution
import org.elaastic.questions.assignment.sequence.interaction.results.ResponsesDistributionOnAttempt
import org.junit.jupiter.api.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.BeforeEach


internal class ResponsesDistributionConverterTest {

    lateinit var responseDistributionConverter: ResponseDistributionConverter

    @BeforeEach
    fun setUp() {
        responseDistributionConverter = ResponseDistributionConverter()
    }


    @Test
    fun `result should be preserved by the conversion to database combined with the conversion from database`() {
        // Given
        val result = ResponsesDistribution(
                ResponsesDistributionOnAttempt(10, arrayOf(4, 5), 1),
                ResponsesDistributionOnAttempt(10, arrayOf(6, 4), 0)
        )

        // Expect
        assertThat(
                result
                        .let { responseDistributionConverter.convertToDatabaseColumn(it) }
                        .let { responseDistributionConverter.convertToEntityAttribute(it) },
                equalTo(result)
        )
    }

}