package org.elaastic.consolidation.subject.question.specification

import org.elaastic.material.instructional.question.QuestionType

class ExclusiveChoiceQuestionSpecification(
    val nbCandidateItem: Int,
    val expectedChoiceIndex: Int,
) : QuestionSpecification {
    override val questionType = QuestionType.ExclusiveChoice
}