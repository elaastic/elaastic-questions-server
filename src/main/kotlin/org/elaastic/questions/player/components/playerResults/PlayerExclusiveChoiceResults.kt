package org.elaastic.questions.player.components.playerResults

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.player.components.explanationViewer.ExplanationData

class PlayerExclusiveChoiceResults(
        override val statementType: QuestionType,
        override val explanationFirstTry: ExplanationData?,
        override val explanationSecondTry: ExplanationData?,
        val choiceFirstTry: LearnerChoice?,
        val choiceSecondTry: LearnerChoice?,
        val scoreFirstTry: Int?,
        val scoreSecondTry: Int?,
        val expectedChoice: ExclusiveChoiceSpecification,
    ) : PlayerResultsModel{

    override fun areBothResponsesEqual(): Boolean =
            arrayOf(choiceFirstTry).contentEquals(arrayOf(choiceSecondTry))

    fun isCorrectAnswer(itemId : Int) : Boolean =
            itemId == expectedChoice.expectedChoice.index

    fun isFirstChoiceEmpty() : Boolean =
            choiceFirstTry == null

    fun isSecondChoiceEmpty() : Boolean =
            choiceSecondTry == null
}