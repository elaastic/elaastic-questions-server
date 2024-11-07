package org.elaastic.player.results.learner

import org.elaastic.player.explanations.ExplanationData
import org.elaastic.questions.assignment.QuestionType

class LearnerOpenResults(
    override val explanationFirstTry: ExplanationData?,
    override val explanationSecondTry: ExplanationData?,
    ) : LearnerResultsModel {

    override fun getQuestionType() = QuestionType.OpenEnded

    override fun hasAnsweredPhase1() = explanationFirstTry != null
    override fun hasAnsweredPhase2() = explanationSecondTry != null

    override fun areBothResponsesEqual() : Boolean = explanationFirstTry?.content == explanationSecondTry?.content

    override fun areBothExplanationsEqual(): Boolean = areBothResponsesEqual()

}