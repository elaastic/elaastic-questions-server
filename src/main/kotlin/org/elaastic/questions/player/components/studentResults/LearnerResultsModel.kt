package org.elaastic.questions.player.components.studentResults

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.player.components.explanationViewer.ExplanationData

interface LearnerResultsModel {
    val explanationFirstTry: ExplanationData?
    val explanationSecondTry: ExplanationData?

    fun getQuestionType(): QuestionType

    fun areBothResponsesEqual(): Boolean?

    fun areBothExplanationsEqual(): Boolean?

    fun hasAnsweredPhase1(): Boolean

    fun hasAnsweredPhase2(): Boolean
}