package org.elaastic.questions.test.interpreter

import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.test.interpreter.command.*

class FunctionalTestInterpreter : Interpreter() {

    override fun parseCommand(command: String, args: List<String>): Command {
        when (command) {
            CommandDescriptor.SUBMIT_RESPONSE.command -> {
                require(args.size == 4 || args.size == 5) { CommandDescriptor.SUBMIT_RESPONSE.displayUsage() }
                return SubmitResponse(
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
                return Evaluate(
                    username = args[0],
                    strategy = EvaluationStrategy.valueOf(args[1]),
                )
            }
            
            else -> error("Unknown command: $command")
        }
    }
}