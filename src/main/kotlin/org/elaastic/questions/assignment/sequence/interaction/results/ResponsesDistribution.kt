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

package org.elaastic.questions.assignment.sequence.interaction.results

import com.fasterxml.jackson.annotation.JsonIgnore
import java.lang.IllegalArgumentException

typealias AttemptNum = Int
typealias ResponsePercentage = Float

data class ResponsesDistribution(
        val resultForAttempt1: ResponsesDistributionOnAttempt,
        val resultForAttempt2: ResponsesDistributionOnAttempt? = null
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
    fun getResultForAttemptN(n: Int): ResponsesDistributionOnAttempt =
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
