package org.elaastic.questions.player.components.playerResults

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.player.components.explanationViewer.ExplanationData

class PlayerOpenResults(
        override val statementType : QuestionType,
        override val explanationFirstTry: ExplanationData?,
        override val explanationSecondTry: ExplanationData?,
    ) : PlayerResultsModel {

    override fun areBothResponsesEqual() : Boolean = explanationFirstTry?.content.equals(explanationSecondTry?.content)
}