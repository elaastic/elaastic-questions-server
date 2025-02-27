package org.elaastic.player.chart.confidence

import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.activity.results.ItemIndex
import org.elaastic.player.chart.response.ChoiceSpecificationData

data class ConfidenceDistributionChartModel(
    val interactionId: Long,
    val choiceSpecification: ChoiceSpecificationData,
    val results: Map<ItemIndex, Map<ConfidenceDegree, Int>>
)