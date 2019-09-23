package org.elaastic.questions.assignment.sequence.interaction

import com.fasterxml.jackson.annotation.JsonIgnore
import java.lang.IllegalArgumentException

typealias AttemptNum = Int
typealias ResponsePercentage = Float

data class InteractionResult(
        val resultForAttempt1: ResultOfGroupOnAttempt,
        val resultForAttempt2: ResultOfGroupOnAttempt? = null
) {

    init {
        require(
                resultForAttempt2 == null || resultForAttempt1.size() == resultForAttempt2.size()
        )
    }

    @JsonIgnore
    fun getNbOfAttempt(): Int =
            if (resultForAttempt2 == null) 1 else 2

    @JsonIgnore
    fun getResultForAttemptN(n: Int): ResultOfGroupOnAttempt =
            when (n) {
                1 -> resultForAttempt1
                2 -> resultForAttempt2 ?: throw IllegalArgumentException("This interaction result has only one attempt")
                else -> throw IllegalArgumentException("$n is not a valid number of attempt")
            }

    @JsonIgnore
    fun hasAnyResult() =
            resultForAttempt1.nbResponse > 0 ||
                    (resultForAttempt2 != null && resultForAttempt2.nbResponse > 0)

    fun toLegacyFormat(): Map<AttemptNum, Map<ItemIndex, ResponsePercentage>> {
        val data = mutableMapOf<AttemptNum, Map<ItemIndex, ResponsePercentage>>()

        for (numAttempt in 1..getNbOfAttempt()) {
            val dataAttempt = mutableMapOf<ItemIndex, ResponsePercentage>()
            getResultForAttemptN(numAttempt).let {
                dataAttempt[0] = percentOf(it.nbNoItem, it.nbResponse)
                for (i in 1..it.size()) {
                    dataAttempt[i] = percentOf(it.getNbVotes(i), it.nbResponse)
                }
            }
            data[numAttempt] = dataAttempt
        }

        return data
    }

    private fun percentOf(nbVote: Int, nbResponse: Int): Float {
        return (100 * nbVote).toFloat() / nbResponse.toFloat()
    }
}