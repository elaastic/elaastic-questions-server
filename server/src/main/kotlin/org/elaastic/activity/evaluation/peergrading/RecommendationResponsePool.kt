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

package org.elaastic.activity.evaluation.peergrading

import org.elaastic.activity.evaluation.ResponseId
import java.util.*

class RecommendationResponsePool(responseList: List<ResponseInfo>,
                                 comparator: Comparator<ResponseInfo>) {

    var comparator: Comparator<ResponseInfo> = comparator
        set(value) {
            responseSet.let {
                responseSet = TreeSet(value)
                responseSet.addAll(it)
            }

            field = value
        }

    fun comparator(value: Comparator<ResponseInfo>): RecommendationResponsePool {
        comparator = value
        return this
    }

    private var responseSet: TreeSet<ResponseInfo> = TreeSet(comparator)

    init {
        responseSet.addAll(responseList)
    }


    fun next(except: Collection<ResponseId> = listOf()): ResponseInfo? {
        val refusedCandidates = mutableListOf<ResponseInfo>()

        var candidate: ResponseInfo? = null
        while (candidate == null && !responseSet.isEmpty()) {
            candidate = responseSet.pollLast()
            if (candidate.id in except) {
                refusedCandidates.add(candidate)
                candidate = null
            }
        }

        responseSet.addAll(refusedCandidates) //re-queue all the refused candidates
        if (candidate != null) {
            candidate.nbSelection++
            responseSet.add(candidate) // re-queue the selected candidate
        }

        return candidate
    }
}