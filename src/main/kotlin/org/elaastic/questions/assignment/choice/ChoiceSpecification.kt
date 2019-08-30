package org.elaastic.questions.assignment.choice

import org.elaastic.questions.assignment.choice.legacy.ChoiceSpecification

interface ChoiceSpecification {

    fun getChoiceType() : ChoiceType

    var nbCandidateItem : Int

    fun toLegacy(): ChoiceSpecification
}