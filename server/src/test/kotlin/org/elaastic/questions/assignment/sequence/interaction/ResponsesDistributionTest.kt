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

package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.sequence.interaction.results.ResponsesDistribution
import org.elaastic.questions.assignment.sequence.interaction.results.ResponsesDistributionOnAttempt
import org.elaastic.questions.test.directive.tNoProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test



internal class ResponsesDistributionTest {

    @Test
    fun `instantiate an InteractionResult with a single attempt`() {
        ResponsesDistribution(
                ResponsesDistributionOnAttempt(2, arrayOf(2, 0), 0)
        ).tNoProblem()
    }

    @Test
    fun `instantiate an InteractionResult with 2 attempts`() {
        ResponsesDistribution(
                ResponsesDistributionOnAttempt(2, arrayOf(2, 0), 0),
                ResponsesDistributionOnAttempt(2, arrayOf(1, 1), 0)
        ).tNoProblem()
    }

    @Test
    fun `can't instantiate an InteractionResult with 2 attempts with different number of values`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ResponsesDistribution(
                    ResponsesDistributionOnAttempt(0, arrayOf(2, 0, 4)),
                    ResponsesDistributionOnAttempt(0, arrayOf(2, 0))
            )
        }
    }
    
}
