package org.elaastic.questions.player.components.responseDistributionChart

import org.elaastic.questions.assignment.sequence.interaction.results.AttemptNum
import org.elaastic.questions.assignment.sequence.interaction.results.ItemIndex
import org.elaastic.questions.assignment.sequence.interaction.results.ResponsePercentage

data class ResponseDistributionChartModel(
        val interactionId: Long,
        val choiceSpecification: ChoiceSpecificationData,
        val results: Map<AttemptNum, Map<ItemIndex, ResponsePercentage>>
)