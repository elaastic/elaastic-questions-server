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

import org.elaastic.test.interpreter.command.Command

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