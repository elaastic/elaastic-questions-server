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

import org.elaastic.questions.assignment.sequence.ConfidenceDegree

typealias NumberOfOccurence = Int

data class ConfidenceDistribution(
        val confidencePerChoice: List<ConfidenceDistributionOnResponse>
) {
    fun toJSON(): Map<ItemIndex, Map<ConfidenceDegree, NumberOfOccurence>> {
        val data = mutableMapOf<ItemIndex, Map<ConfidenceDegree, NumberOfOccurence>>()

        for (i in 0..confidencePerChoice.size - 1) {
            val currentChoice = confidencePerChoice[i]
            val dataResponse = mutableMapOf<ConfidenceDegree, NumberOfOccurence>()
            for (currentConfidence in ConfidenceDegree.values()) {
//                dataResponse[currentConfidence] = percentOf(currentChoice.nbResponseByConfidence[currentConfidence.ordinal], currentChoice.nbResponse)
                dataResponse[currentConfidence] = currentChoice.nbResponseByConfidence[currentConfidence.ordinal]
            }
            data[i] = dataResponse
        }

        return data
    }
}
