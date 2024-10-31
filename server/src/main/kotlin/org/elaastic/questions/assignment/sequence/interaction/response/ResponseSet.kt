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

import org.elaastic.questions.assignment.sequence.interaction.results.AttemptNum

class ResponseSet(responses: List<Response>) {

    private val responsesByAttempt = Array<MutableList<Response>>(2) { mutableListOf() }

    init {
        responses.forEach { add(it) }
    }

    fun isEmpty(): Boolean =
        get(1).isEmpty() && get(2).isEmpty()

    /**
     * Get the responses of a given attempt.
     * @param i the attempt number (1 or 2)
     * @throws IllegalArgumentException if the attempt number is not 1 or 2
     */
    operator fun get(i: AttemptNum) = when (i) {
        1 -> responsesByAttempt[0]
        2 -> responsesByAttempt[1]
        else -> throw IllegalArgumentException("There is no attempt $i")
    }

    fun getWithoutFake(attempt: AttemptNum) = get(attempt).filter { !it.fake }

    fun add(response: Response) = when (response.attempt) {
        1, 2 -> responsesByAttempt[response.attempt - 1].add(response)
        else -> throw IllegalStateException("Invalid response ; attempt=${response.attempt}")
    }
}
