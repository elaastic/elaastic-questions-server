package org.elaastic.questions.player.components.studentResults

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.player.components.explanationViewer.ExplanationData

class LearnerExclusiveChoiceResults(
    override val explanationFirstTry: ExplanationData?,
    override val explanationSecondTry: ExplanationData?,
    val choiceFirstTry: LearnerChoice?,
    val choiceSecondTry: LearnerChoice?,
    val scoreFirstTry: Int?,
    val scoreSecondTry: Int?,
    val expectedChoice: ExclusiveChoiceSpecification,
    ) : LearnerResultsModel{

    override fun getQuestionType() = QuestionType.ExclusiveChoice

    override fun hasAnsweredPhase1() = choiceFirstTry != null
    override fun hasAnsweredPhase2() = choiceSecondTry != null

    override fun areBothResponsesEqual(): Boolean =
        choiceFirstTry == choiceSecondTry

    fun isCorrectAnswer(itemId : Int) : Boolean =
            itemId == expectedChoice.expectedChoice.index

    fun isFirstChoiceEmpty() : Boolean =
            choiceFirstTry == null

    fun isSecondChoiceEmpty() : Boolean =
            choiceSecondTry == null
}