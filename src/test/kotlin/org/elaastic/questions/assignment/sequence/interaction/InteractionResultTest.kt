package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.test.directive.tNoProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test



internal class InteractionResultTest {

    @Test
    fun `instantiate an InteractionResult with a single attempt`() {
        InteractionResult(
                ResultOfGroupOnAttempt(2, listOf(2, 0), 0)
        ).tNoProblem()
    }

    @Test
    fun `instantiate an InteractionResult with 2 attempts`() {
        InteractionResult(
                ResultOfGroupOnAttempt(2, listOf(2, 0), 0),
                ResultOfGroupOnAttempt(2, listOf(1, 1), 0)
        ).tNoProblem()
    }

    @Test
    fun `can't instantiate an InteractionResult with 2 attempts with different number of values`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            InteractionResult(
                    ResultOfGroupOnAttempt(0, listOf(2, 0, 4)),
                    ResultOfGroupOnAttempt(0, listOf(2, 0))
            )
        }
    }
    
}