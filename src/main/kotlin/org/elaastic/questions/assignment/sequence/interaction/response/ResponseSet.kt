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