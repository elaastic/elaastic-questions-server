package org.elaastic.questions.test.interpreter.command

import org.elaastic.questions.assignment.sequence.ConfidenceDegree

class SubmitResponse(
    val phase: Phase,
    val username: String,
    val correct: Boolean,
    val confidenceDegree: ConfidenceDegree,
    val explanation: String? = null,
) : Command {

}