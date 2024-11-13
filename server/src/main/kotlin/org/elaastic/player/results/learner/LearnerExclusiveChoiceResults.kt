package org.elaastic.player.results.learner

import org.elaastic.material.instructional.question.ExclusiveChoiceSpecification
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.material.instructional.question.legacy.LearnerChoice
import org.elaastic.player.explanations.ExplanationData

class LearnerExclusiveChoiceResults(
    override val explanationFirstTry: ExplanationData?,
    override val explanationSecondTry: ExplanationData?,
    val choiceFirstTry: LearnerChoice?,
    val choiceSecondTry: LearnerChoice?,
    val scoreFirstTry: Int?,
    val scoreSecondTry: Int?,
    val expectedChoice: ExclusiveChoiceSpecification,
) : LearnerResultsModel {

    override fun getQuestionType() = QuestionType.ExclusiveChoice

    override fun hasAnsweredPhase1() = choiceFirstTry != null
    override fun hasAnsweredPhase2() = choiceSecondTry != null

    override fun areBothResponsesEqual(): Boolean =
        choiceFirstTry == choiceSecondTry

    override fun areBothExplanationsEqual(): Boolean =
        (explanationFirstTry?.content == explanationSecondTry?.content)

    fun isCorrectAnswer(itemId: Int): Boolean =
        itemId == expectedChoice.expectedChoice.index

    fun isFirstChoiceEmpty(): Boolean =
        choiceFirstTry == null

    fun isSecondChoiceEmpty(): Boolean =
        choiceSecondTry == null
}