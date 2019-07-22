package org.elaastic.questions.assignment.choice

import javax.validation.constraints.Max

/**
 * @author John Tranier
 */
data class ChoiceItemSpecification(
        var index: Int,
        @field:Max(100) var score: Float
)