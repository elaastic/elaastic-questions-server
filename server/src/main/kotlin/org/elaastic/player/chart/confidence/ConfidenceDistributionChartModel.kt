package org.elaastic.player.chart.confidence

import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.player.chart.response.ChoiceSpecificationData
import org.elaastic.sequence.interaction.results.ItemIndex

data class ConfidenceDistributionChartModel(
    val interactionId: Long,
    val choiceSpecification: ChoiceSpecificationData,
    val results: Map<ItemIndex, Map<ConfidenceDegree, Int>>
)