package org.elaastic.questions.api.practice.subject.question.specification

import org.elaastic.questions.assignment.QuestionType

interface QuestionSpecification {
    val questionType: QuestionType
}