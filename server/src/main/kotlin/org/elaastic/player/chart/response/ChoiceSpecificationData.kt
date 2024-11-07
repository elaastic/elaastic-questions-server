package org.elaastic.player.chart.response

import org.elaastic.questions.assignment.choice.ChoiceSpecification
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification

data class ChoiceSpecificationData(
        val itemCount: Int,
        val expectedChoiceList: List<Int>
) {
    constructor(value: ChoiceSpecification): this(
            value.nbCandidateItem,
            when(value) {
                is ExclusiveChoiceSpecification -> listOf(value.expectedChoice.index)
                is MultipleChoiceSpecification -> value.expectedChoiceList.map { it.index }
                else -> error("Unsupported implementation of ChoiceSpecification")
            }
    )
}