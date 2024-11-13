package org.elaastic.player.results.learner

import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.player.explanations.ExplanationData

interface LearnerResultsModel {
    val explanationFirstTry: ExplanationData?
    val explanationSecondTry: ExplanationData?

    fun getQuestionType(): QuestionType

    fun areBothResponsesEqual(): Boolean?

    fun areBothExplanationsEqual(): Boolean?

    fun hasAnsweredPhase1(): Boolean

    fun hasAnsweredPhase2(): Boolean
}