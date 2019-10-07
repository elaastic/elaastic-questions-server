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

package org.elaastic.questions.player.components.results

import org.elaastic.questions.player.PlayerController
import org.elaastic.questions.player.components.responseDistributionChart.ResponseDistributionChartModel

data class ChoiceResultsModel(
        override val sequenceIsStopped: Boolean,
        override val sequenceId: Long,
        override val interactionId: Long,
        override val interactionRank: Int,
        val hasAnyResult: Boolean,
        val responseDistributionChartModel: ResponseDistributionChartModel? = null,
        override val hasExplanations: Boolean,
        override val explanationViewerModel: PlayerController.ExplanationViewerModel? = null
) : ResultsModel {
    override fun getHasChoices() = true
}
