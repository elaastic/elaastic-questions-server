package org.elaastic.player.chart.response

import org.elaastic.material.instructional.question.ChoiceSpecification
import org.elaastic.material.instructional.question.ExclusiveChoiceSpecification
import org.elaastic.material.instructional.question.MultipleChoiceSpecification

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