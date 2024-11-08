package org.elaastic.questions.test.interpreter.command

import org.elaastic.sequence.ExecutionContext
import org.elaastic.activity.response.ConfidenceDegree

enum class CommandDescriptor(
    val command: String,
    val usage: String,
) {
    START_SEQUENCE(
    command = "start",
    usage = "start      <context>",
    ),
    SUBMIT_RESPONSE(
    command = "response",
    usage = "response   <phase> <username> <correctness> <confidence-degree> [<explanation>]"
    ),
    EVAL(
    command = "eval",
    usage = "eval       <username> <evaluation-strategy>",
    ),

    PUBLISH_RESULTS(
    command = "publish",
    usage = "publish"
    ),

    NEXT_PHASE(
        command = "next",
        usage = "next"
    ),

    STOP_SEQUENCE(
        command = "stop",
        usage = "stop"
    );


    fun displayUsage() = "Usage: $usage"

    companion object {
        val help =
            values().joinToString("\n") { it.usage } + "\n" +
                    """
                        ---       
                        <context>               ::= ${ExecutionContext.values().joinToString(" | ")}                 
                        <phase>                 ::= 1 | 2
                        <correctness>           ::= correct | *
                        <confidence-degree>     ::= ${ConfidenceDegree.values().joinToString(" | ")}
                        <evaluation-strategy>   ::= ${EvaluationStrategy.values().joinToString(" | ")}
                    """.trimIndent()
    }
}
