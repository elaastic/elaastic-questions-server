package org.elaastic.questions.test.interpreter.command

import org.elaastic.sequence.ExecutionContext

/**
 * Command for starting a sequence
 * The sequence will be started by the owner teacher
 *
 * @author John Tranier
 */
class StartSequence(
    val executionContext: ExecutionContext,
    val studentsProvideExplanation: Boolean = true,
    val nbResponseToEvaluate: Int = 3
) : Command {

}