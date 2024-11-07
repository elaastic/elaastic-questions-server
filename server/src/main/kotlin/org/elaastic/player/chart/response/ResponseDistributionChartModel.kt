package org.elaastic.player.chart.response

import org.elaastic.sequence.interaction.results.AttemptNum
import org.elaastic.sequence.interaction.results.ItemIndex
import org.elaastic.sequence.interaction.results.ResponsePercentage

data class ResponseDistributionChartModel(
    val interactionId: Long,
    val choiceSpecification: ChoiceSpecificationData,
    val results: Map<AttemptNum, Map<ItemIndex, ResponsePercentage>>
)