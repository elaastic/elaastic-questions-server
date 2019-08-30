package org.elaastic.questions.assignment.choice.legacy

import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.sequence.TeacherExplanation
import javax.validation.constraints.Max
import javax.validation.constraints.Min


data class ChoiceSpecification(
        var choiceInteractionType: String,
        @field:Max(10) var itemCount: Int,
        @field:Min(1) var expectedChoiceList: List<ChoiceItem> = listOf(),
        var explanationChoiceList: List<TeacherExplanation> = listOf()
)

