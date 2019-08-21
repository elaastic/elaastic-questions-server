package org.elaastic.questions.assignment.choice

import com.fasterxml.jackson.annotation.JsonIgnore
import org.elaastic.questions.assignment.sequence.TeacherExplanation
import javax.persistence.Transient
import javax.validation.constraints.Max
import javax.validation.constraints.Min


data class ChoiceSpecification(
        var choiceInteractionType: ChoiceInteractionType,
        @field:Max(10) var itemCount: Int,
        @field:Min(1) var expectedChoiceList: List<ChoiceItemSpecification> = listOf(),
        var explanationChoiceList: List<TeacherExplanation> = listOf()
) {
    @Transient
    @JsonIgnore
    fun isMultiple(): Boolean {
        return choiceInteractionType == ChoiceInteractionType.MULTIPLE
    }

    @Transient
    @JsonIgnore
    fun isExclusive(): Boolean {
        return choiceInteractionType == ChoiceInteractionType.EXCLUSIVE
    }

    @Transient
    @JsonIgnore
    fun getExpectedChoice(): ChoiceItemSpecification? {
        return when (choiceInteractionType) {
            ChoiceInteractionType.MULTIPLE -> null
            ChoiceInteractionType.EXCLUSIVE -> expectedChoiceList.first()
        }
    }

    @Transient
    fun setExpectedChoice(value: ChoiceItemSpecification) {
        expectedChoiceList = listOf(value)
    }
}

