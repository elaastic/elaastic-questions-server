package org.elaastic.questions.player.components.studentResults

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.player.explanations.ExplanationData

class LearnerMultipleChoiceResults(
    override val explanationFirstTry: ExplanationData?,
    override val explanationSecondTry: ExplanationData?,
    val choiceFirstTry: LearnerChoice?,
    val choiceSecondTry: LearnerChoice?,
    val scoreFirstTry: Int?,
    val scoreSecondTry: Int?,
    val expectedChoice: MultipleChoiceSpecification,
    ) : LearnerResultsModel{

    override fun getQuestionType() = QuestionType.MultipleChoice

    override fun hasAnsweredPhase1() = choiceFirstTry != null
    override fun hasAnsweredPhase2() = choiceSecondTry != null

    override fun areBothResponsesEqual(): Boolean = choiceFirstTry == choiceSecondTry

    override fun areBothExplanationsEqual(): Boolean = (explanationFirstTry?.content == explanationSecondTry?.content)

    fun isCorrectAnswer(itemId: Int) : Boolean =
            expectedChoice.expectedChoiceList.any { choiceItem ->  choiceItem.index == itemId }

    fun isFirstChoiceEmpty() : Boolean =
            choiceFirstTry == null

    fun isSecondChoiceEmpty() : Boolean =
            choiceSecondTry == null
}