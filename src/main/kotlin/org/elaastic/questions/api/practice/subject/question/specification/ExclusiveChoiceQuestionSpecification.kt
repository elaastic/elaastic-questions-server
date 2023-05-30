package org.elaastic.questions.api.practice.subject.question.specification

import org.elaastic.questions.assignment.QuestionType

class ExclusiveChoiceQuestionSpecification(
    val nbCandidateItem: Int,
    val expectedChoiceIndex: Int,
) : QuestionSpecification {
    override val questionType = QuestionType.ExclusiveChoice
}