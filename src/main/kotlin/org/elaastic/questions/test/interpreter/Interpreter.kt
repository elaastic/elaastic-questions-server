package org.elaastic.questions.test.interpreter

import org.elaastic.questions.test.interpreter.command.Command

abstract class Interpreter {

    fun parse(script: String) : List<Command> =
        script.lines()
            .filter { it.isNotBlank() }
            .map { line ->
            val input = line.trim().split(Regex( "\\s(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")).map { it.replace("\"", "") }
            val cmd = input[0]
            val args = input.drop(1)
            parseCommand(cmd, args)
        }


    abstract fun parseCommand(command: String, args: List<String>) : Command

}

typealias Arguments = List<String>