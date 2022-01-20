package org.elaastic.questions.test.interpreter.command

import org.elaastic.questions.assignment.sequence.ConfidenceDegree

class SubmitOpenResponse(
    val username: String,
    val confidenceDegree: ConfidenceDegree,
    val explanation: String? = null
) : Command {
}