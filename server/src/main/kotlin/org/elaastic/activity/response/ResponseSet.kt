package org.elaastic.activity.response

import org.elaastic.activity.results.AttemptNum

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