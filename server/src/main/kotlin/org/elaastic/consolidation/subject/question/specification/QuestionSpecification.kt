package org.elaastic.consolidation.subject.question.specification

import org.elaastic.questions.assignment.QuestionType

interface QuestionSpecification {
    val questionType: QuestionType
}