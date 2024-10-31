package org.elaastic.questions.api.practice.subject.question.specification

import org.elaastic.questions.assignment.QuestionType

class OpenQuestionSpecification : QuestionSpecification {
    override val questionType = QuestionType.OpenEnded
}