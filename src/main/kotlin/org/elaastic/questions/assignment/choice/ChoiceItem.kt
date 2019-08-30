package org.elaastic.questions.assignment.choice

import javax.validation.constraints.Max


data class ChoiceItem(
        var index: Int,
        @field:Max(100) var score: Float
)