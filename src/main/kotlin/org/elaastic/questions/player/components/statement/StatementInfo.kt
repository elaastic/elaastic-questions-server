package org.elaastic.questions.player.components.statement

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.Statement

data class StatementInfo(
        val title: String,
        val questionType: QuestionType,
        val content: String
) {
    constructor(statement: Statement) :
            this(
                    statement.title,
                    statement.questionType,
                    statement.content
            )
}