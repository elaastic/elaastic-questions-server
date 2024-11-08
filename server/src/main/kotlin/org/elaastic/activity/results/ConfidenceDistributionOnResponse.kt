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
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.sequence.interaction.response.Response

class ConfidenceDistributionOnResponse(
    var nbResponse: Int = 0,
    var nbResponseByConfidence: Array<NbVote> = Array(4) { 0 },
    var nbNoItem: Int = 0
) {

    constructor(responses: List<Response>, choice: Int) : this() {
        responses.filter { r -> r.learnerChoice != null && r.learnerChoice!!.contains(choice)}.forEach { add(it) }
    }

    @JsonIgnore
    fun getNbItem(): Int = nbResponseByConfidence.size

    fun size() = getNbItem()

    fun incNbVotes(cf: ConfidenceDegree) {
        nbResponseByConfidence[cf.ordinal]++
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponsesDistributionOnAttempt

        if (nbNoItem != other.nbNoItem) return false
        if (!nbResponseByConfidence.contentEquals(other.nbVotesByItem)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nbNoItem
        result = 31 * result + nbResponseByConfidence.hashCode()
        return result
    }

    fun add(response: Response) {
        nbResponse++
        response.confidenceDegree?.let { incNbVotes(it) }
    }

}
