package org.elaastic.consolidation.subject.question.specification

import org.elaastic.questions.assignment.QuestionType

class OpenQuestionSpecification : QuestionSpecification {
    override val questionType = QuestionType.OpenEnded
}