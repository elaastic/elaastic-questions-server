package org.elaastic.test.interpreter.command

import org.elaastic.activity.response.ConfidenceDegree

class SubmitResponse(
    val phase: Phase,
    val username: String,
    val correct: Boolean,
    val confidenceDegree: ConfidenceDegree,
    val explanation: String? = null,
) : Command {

}