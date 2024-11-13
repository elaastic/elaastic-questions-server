package org.elaastic.player.chart.confidence

import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.player.chart.response.ChoiceSpecificationData
import org.elaastic.activity.results.ItemIndex

data class ConfidenceDistributionChartModel(
    val interactionId: Long,
    val choiceSpecification: ChoiceSpecificationData,
    val results: Map<ItemIndex, Map<ConfidenceDegree, Int>>
)