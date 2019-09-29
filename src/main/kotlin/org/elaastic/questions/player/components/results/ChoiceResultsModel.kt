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