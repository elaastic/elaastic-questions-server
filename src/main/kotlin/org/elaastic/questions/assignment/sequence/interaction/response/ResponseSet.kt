/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.questions.assignment.sequence.interaction.response

import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class ResponseSet(responses: List<Response>) {

    init {
        responses.forEach { add(it) }
    }

    private val responsesByAttempt = Array<MutableList<Response>>(2) { mutableListOf() }

    operator fun get(i: Int) = when (i) {
        1 -> responsesByAttempt[0]
        2 -> responsesByAttempt[1]
        else -> throw IllegalArgumentException("There is no attempt $i")
    }

    fun add(response: Response) = when(response.attempt) {
        0, 1 -> responsesByAttempt[response.attempt-1].add(response)
        else -> throw IllegalStateException("Invalid response ; attempt=${response.attempt}")
    }
}
