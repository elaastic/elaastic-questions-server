package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.test.directive.tNoProblem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


/**
 * @author John Tranier
 */
internal class InteractionResultTest {

    @Test
    fun `instantiate an InteractionResult with a single attempt`() {
        InteractionResult(
                OneAttemptResult(listOf(33.3f,33.3f,33.3f))
        ).tNoProblem()
    }

    @Test
    fun `instantiate an InteractionResult with 2 attempts`() {
        InteractionResult(
                OneAttemptResult(listOf(33.3f,33.3f,33.3f)),
                OneAttemptResult(listOf(50f,0f,50f))
        ).tNoProblem()
    }

    @Test
    fun `can't instantiate an InteractionResult with 2 attempts with different number of values`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            InteractionResult(
                    OneAttemptResult(listOf(33.3f, 33.3f, 33.3f)),
                    OneAttemptResult(listOf(50f, 50f))
            )
        }
    }
    
}