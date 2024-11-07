package org.elaastic.consolidation.subject.question.specification

import org.elaastic.questions.assignment.QuestionType

class MultipleChoiceQuestionSpecification(
    val nbCandidateItem: Int,
    val expectedChoiceIndexList: List<Int>
) : QuestionSpecification {
    override val questionType = QuestionType.MultipleChoice
}