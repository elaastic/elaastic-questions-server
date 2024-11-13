package org.elaastic.player.chart.response

import org.elaastic.activity.results.AttemptNum
import org.elaastic.activity.results.ItemIndex
import org.elaastic.activity.results.ResponsePercentage

data class ResponseDistributionChartModel(
    val interactionId: Long,
    val choiceSpecification: ChoiceSpecificationData,
    val results: Map<AttemptNum, Map<ItemIndex, ResponsePercentage>>
)