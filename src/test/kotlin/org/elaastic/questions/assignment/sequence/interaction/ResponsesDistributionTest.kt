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