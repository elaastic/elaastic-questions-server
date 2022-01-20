package org.elaastic.questions.test.interpreter

import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.test.interpreter.command.Command
import org.elaastic.questions.test.interpreter.command.SubmitExclusiveChoiceResponse
import org.elaastic.questions.test.interpreter.command.SubmitMultipleChoiceResponse
import org.elaastic.questions.test.interpreter.command.SubmitOpenResponse

class FunctionalTestInterpreter : Interpreter() {

    override fun parseCommand(command: String, args: List<String>) : Command {
        when(command) {
            "submit-open" -> {
                require(args.size == 2 || args.size == 3) { "usage: submit-open <who> <confidenceDegree> [<explanation>]" }
                return SubmitOpenResponse(
                    username = args[0],
                    confidenceDegree = ConfidenceDegree.valueOf(args[1]),
                    explanation = if(args.size == 3) args[2] else null
                )
            }

            "submit-exclusive" -> {
                require(args.size == 3 || args.size == 4) { "usage: submit-exclusive <who> <correct | incorrect> <confidenceDegree> [<explanation>]" }
                return SubmitExclusiveChoiceResponse(
                    username = args[0],
                    correct = args[1] == "correct",
                    confidenceDegree = ConfidenceDegree.valueOf(args[2]),
                    explanation = if(args.size == 4) args[3] else null
                )
            }

            "submit-multiple" -> {
                require(args.size == 3 || args.size == 4) { "usage: submit-multiple <who> <correct | incorrect> <confidenceDegree> [<explanation>]" }
                return SubmitMultipleChoiceResponse(
                    username = args[0],
                    correct = args[1] == "correct",
                    confidenceDegree = ConfidenceDegree.valueOf(args[2]),
                    explanation = if(args.size == 4) args[3] else null
                )
            }

            else -> error("Unknown command: $command")
        }
    }
}