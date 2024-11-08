package org.elaastic.player.chart.evaluation

import org.elaastic.player.chart.response.ChoiceSpecificationData
import org.elaastic.activity.results.ItemIndex
import java.math.BigDecimal

data class EvaluationDistributionChartModel(
    val interactionId: Long,
    val choiceSpecification: ChoiceSpecificationData,
    val results: Map<ItemIndex, Map<BigDecimal, Int>>
)