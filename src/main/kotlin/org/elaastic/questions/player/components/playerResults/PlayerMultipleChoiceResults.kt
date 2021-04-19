package org.elaastic.questions.player.components.playerResults

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ChoiceType
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.player.components.explanationViewer.ExplanationData
import java.math.BigDecimal

class PlayerMultipleChoiceResults(
        override val statementType: QuestionType,
        override val explanationFirstTry: ExplanationData?,
        override val explanationSecondTry: ExplanationData?,
        val choiceFirstTry: LearnerChoice?,
        val choiceSecondTry: LearnerChoice?,
        val scoreFirstTry: Int?,
        val scoreSecondTry: Int?,
        val expectedChoice: MultipleChoiceSpecification,
    ) : PlayerResultsModel{

    override fun areBothResponsesEqual(): Boolean =
            arrayOf(choiceFirstTry).contentEquals(arrayOf(choiceSecondTry))

    fun isCorrectAnswer(itemId: Int) : Boolean =
            expectedChoice.expectedChoiceList.any { choiceItem ->  choiceItem.index == itemId }

    fun isFirstChoiceEmpty() : Boolean =
            choiceFirstTry == null

    fun isSecondChoiceEmpty() : Boolean =
            choiceSecondTry == null
}