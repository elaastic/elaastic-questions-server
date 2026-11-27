/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.test.interpreter.command

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
