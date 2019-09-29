package org.elaastic.questions.player.components.responseDistributionChart

import org.elaastic.questions.assignment.sequence.interaction.AttemptNum
import org.elaastic.questions.assignment.sequence.interaction.ItemIndex
import org.elaastic.questions.assignment.sequence.interaction.ResponsePercentage

data class ResponseDistributionChartModel(
        val interactionId: Long,
        val choiceSpecification: ChoiceSpecificationData,
        val results: Map<AttemptNum, Map<ItemIndex, ResponsePercentage>>
)