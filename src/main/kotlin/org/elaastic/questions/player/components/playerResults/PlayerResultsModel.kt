package org.elaastic.questions.player.components.playerResults

import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.sequence.interaction.response.Response

class PlayerResultsModel (
        val responseFirstTry: Response?,
        val responseSecondTry: Response?,
        val areBothResponsesEqual: Boolean?,
        val statementHasChoice : Boolean,
        val statementNbItems: Int?,
        var statementExpectedChoiceList: List<Int>?) {

    fun buildStatementExpectedChoiceList(choices: List<ChoiceItem>) {
        var expectedChoice: MutableList<Int> = mutableListOf()
        for(choiceItem: ChoiceItem in choices) {
            expectedChoice.add(choiceItem.index)
        }
        this.statementExpectedChoiceList = expectedChoice
    }

    fun hasAnswered() : Boolean = responseFirstTry!=null

    fun hasAnsweredSecondPhase() : Boolean = responseSecondTry!=null
}