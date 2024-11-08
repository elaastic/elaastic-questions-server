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
import org.elaastic.activity.evaluation.peergrading.PeerGrading
import java.math.BigDecimal

class GradingDistributionOnResponse(
    var nbGrade: Int = 0,
    var nbResponsesByEvaluation: Array<NbVote> = Array(5) { 0 },
    var nbNoItem: Int = 0
) {

    constructor(peerGradings: List<PeerGrading>, choice: Int) : this() {
        val pg =
            peerGradings.filter { pg -> pg.response.learnerChoice != null && pg.response.learnerChoice!!.contains(choice) }
        pg.forEach { add(it) }
    }

    @JsonIgnore
    fun getNbItem(): Int = nbResponsesByEvaluation.size

    fun size() = getNbItem()

    fun incNbVotes(grade: BigDecimal) {
        // TODO : check how we should round here...  nbResponsesByEvaluation[grade.intValueExact() - 1]++
        nbResponsesByEvaluation[grade.toInt() - 1]++
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponsesDistributionOnAttempt

        if (nbNoItem != other.nbNoItem) return false
        if (!nbResponsesByEvaluation.contentEquals(other.nbVotesByItem)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nbNoItem
        result = 31 * result + nbResponsesByEvaluation.hashCode()
        return result
    }

    fun add(peerGrading: PeerGrading) {
        nbGrade++
        peerGrading.grade?.let { incNbVotes(it) }
    }

}
