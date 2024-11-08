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

package org.elaastic.activity.results

import com.fasterxml.jackson.annotation.JsonIgnore
import org.elaastic.sequence.interaction.response.Response


typealias ItemIndex = Int
typealias NbVote = Int

class ResponsesDistributionOnAttempt(
    var nbResponse: Int,
    var nbVotesByItem: Array<NbVote>,
    var nbNoItem: Int = 0
) {
    // Construct empty result for a number of items
    constructor(nbItem: Int) : this(
            0,
            Array(nbItem) { 0 },
            0
    )

    constructor(nbItem: Int, responses: List<Response>) : this(nbItem) {
        responses.forEach { add(it) }
    }

    @JsonIgnore
    fun getNbItem(): Int = nbVotesByItem.size

    fun size() = getNbItem()

    fun getNbVotes(i: ItemIndex): Int {
        return nbVotesByItem[i - 1]
    }

    fun incNbVotes(i: ItemIndex) {
        nbVotesByItem[i - 1]++
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponsesDistributionOnAttempt

        if (nbNoItem != other.nbNoItem) return false
        if (!nbVotesByItem.contentEquals(other.nbVotesByItem)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nbNoItem
        result = 31 * result + nbVotesByItem.hashCode()
        return result
    }

    fun add(response: Response) {
        nbResponse++
        response.learnerChoice.let { choices ->
            if (choices?.isEmpty() != false)
                nbNoItem++
            else choices.forEach { incNbVotes(it) }
        }

    }
}
