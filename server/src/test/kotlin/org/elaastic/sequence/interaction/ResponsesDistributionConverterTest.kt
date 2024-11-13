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

package org.elaastic.sequence.interaction

import org.elaastic.activity.results.ResponseDistributionConverter
import org.elaastic.activity.results.ResponsesDistribution
import org.elaastic.activity.results.ResponsesDistributionOnAttempt
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
