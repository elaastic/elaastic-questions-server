package org.elaastic.questions.assignment.choice

import org.elaastic.questions.assignment.sequence.TeacherExplanation
import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class MultipleChoiceSpecification(
        @field:Max(10) override var nbCandidateItem: Int,
        @field:Min(1) var expectedChoiceList: List<ChoiceItem> = listOf(),
        var explanationChoiceList: List<TeacherExplanation> = listOf()
) : ChoiceSpecification {

    override fun getChoiceType(): ChoiceType {
        return ChoiceType.MULTIPLE
    }

    override fun toLegacy(): org.elaastic.questions.assignment.choice.legacy.ChoiceSpecification {
        return org.elaastic.questions.assignment.choice.legacy.ChoiceSpecification(
                choiceInteractionType = getChoiceType().name,
                itemCount = nbCandidateItem,
                expectedChoiceList = expectedChoiceList,
                explanationChoiceList = explanationChoiceList
        )

    }
}