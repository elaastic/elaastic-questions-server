package org.elaastic.questions.player.components.playerResults

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.player.components.explanationViewer.ExplanationData

interface PlayerResultsModel
{
        val statementType: QuestionType
        val explanationFirstTry : ExplanationData?
        val explanationSecondTry: ExplanationData?

    fun areBothResponsesEqual() : Boolean?

    fun hasAnswered() : Boolean = explanationFirstTry!=null

    fun hasAnsweredSecondPhase() : Boolean = explanationSecondTry!=null
}