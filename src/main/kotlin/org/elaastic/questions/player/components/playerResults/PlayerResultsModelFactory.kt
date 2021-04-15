package org.elaastic.questions.player.components.playerResults

import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.subject.statement.Statement

object PlayerResultsModelFactory {

    fun build(responseFirstTry: Response?,
                 responseSecondTry: Response?,
                 statement: Statement) : PlayerResultsModel {
        var playerResultsModel: PlayerResultsModel =
                PlayerResultsModel(
                        responseFirstTry = responseFirstTry,
                        responseSecondTry = responseSecondTry,
                        areBothResponsesEqual = false,
                        statementHasChoice = statement.hasChoices(),
                        statementNbItems = statement.choiceSpecification?.toLegacy()?.itemCount,
                        statementExpectedChoiceList = null
                )
        if(statement.hasChoices()){
            playerResultsModel.buildStatementExpectedChoiceList(statement.choiceSpecification?.toLegacy()?.expectedChoiceList!!)
        }
        return playerResultsModel
    }

}