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

package org.elaastic.test.interpreter

import org.elaastic.sequence.ExecutionContext
import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.test.interpreter.command.*

class FunctionalTestInterpreter : Interpreter() {

    override fun parseCommand(command: String, args: List<String>) =
        when (command) {
            CommandDescriptor.START_SEQUENCE.command -> {
                require(args.size == 1) { CommandDescriptor.START_SEQUENCE.displayUsage() }
                StartSequence(
                    executionContext = ExecutionContext.valueOf(args[0])
                )
            }

            CommandDescriptor.SUBMIT_RESPONSE.command -> {
                require(args.size == 4 || args.size == 5) { CommandDescriptor.SUBMIT_RESPONSE.displayUsage() }
                SubmitResponse(
                    phase = when (args[0]) {
                        "1" -> Phase.PHASE_1
                        "2" -> Phase.PHASE_2
                        else -> error("${args[0]} is not a valid phase")
                    },
                    username = args[1],
                    correct = args[2] == "correct",
                    confidenceDegree = ConfidenceDegree.valueOf(args[3]),
                    explanation = if (args.size == 5) args[4] else null,
                )
            }

            CommandDescriptor.EVAL.command -> {
                require(args.size == 2) { CommandDescriptor.EVAL.displayUsage() }
                Evaluate(
                    username = args[0],
                    strategy = EvaluationStrategy.valueOf(args[1]),
                )
            }

            CommandDescriptor.NEXT_PHASE.command -> NextPhase()

            CommandDescriptor.PUBLISH_RESULTS.command -> PublishResults()

            CommandDescriptor.STOP_SEQUENCE.command -> StopSequence()

            else -> error("Unknown command: $command")
        }
}