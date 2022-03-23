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

import java.math.BigDecimal


data class GradingDistribution(
        val evaluationPerChoice: List<GradingDistributionOnResponse>
) {
    fun toLegacyFormat(): Map<ItemIndex, Map<BigDecimal, Int>> {
        val data = mutableMapOf<ItemIndex, Map<BigDecimal, Int>>()

        for (i in 0..evaluationPerChoice.size - 1) {
            val currentChoice = evaluationPerChoice[i]
            val dataResponse = mutableMapOf<BigDecimal, Int>()
            for (currentEvaluation in 0..4) {
                dataResponse[currentEvaluation.toBigDecimal()] = currentChoice.nbResponsesByEvaluation[currentEvaluation]
            }
            data[i] = dataResponse
        }

        return data
    }
}
