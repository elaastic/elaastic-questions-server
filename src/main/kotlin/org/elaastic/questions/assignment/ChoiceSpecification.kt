package org.elaastic.questions.assignment

import javax.validation.constraints.Max
import javax.validation.constraints.Min

/**
 * @author John Tranier
 */
data class ChoiceSpecification(
        var choiceInteractionType: ChoiceInteractionType,
        @field:Max(10) var itemCount: Int,
        @field:Min(1) var expectedChoiceList: List<ChoiceItemSpecification> = listOf(),
        var explanationChoiceList: List<TeacherExplanation> = listOf()
)

