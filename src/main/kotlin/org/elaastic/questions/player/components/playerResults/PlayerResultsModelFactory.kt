package org.elaastic.questions.player.components.playerResults

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.player.components.explanationViewer.ExplanationData
import org.elaastic.questions.subject.statement.Statement

object PlayerResultsModelFactory {

    fun buildOpenResult(responseFirstTry: Response?,
                        responseSecondTry: Response?) : PlayerOpenResults =
            PlayerOpenResults(
                    statementType = QuestionType.OpenEnded,
                    explanationFirstTry = if(responseFirstTry != null) ExplanationData(responseFirstTry) else null,
                    explanationSecondTry = if(responseSecondTry != null) ExplanationData(responseSecondTry) else null
            )

    fun buildMultipleChoiceResult(responseFirstTry: Response?,
                                  responseSecondTry: Response?,
                                  statement: Statement) : PlayerMultipleChoiceResults =
            PlayerMultipleChoiceResults(
                    statementType = QuestionType.MultipleChoice,
                    explanationFirstTry = if(responseFirstTry != null) ExplanationData(responseFirstTry) else null,
                    explanationSecondTry = if(responseSecondTry != null) ExplanationData(responseSecondTry) else null,
                    choiceFirstTry = responseFirstTry?.learnerChoice,
                    choiceSecondTry = responseSecondTry?.learnerChoice,
                    scoreFirstTry = responseFirstTry?.score?.intValueExact(),
                    scoreSecondTry = responseSecondTry?.score?.intValueExact(),
                    expectedChoice = MultipleChoiceSpecification(
                            nbCandidateItem = statement.choiceSpecification?.toLegacy()?.itemCount!!,
                            expectedChoiceList = statement.choiceSpecification?.toLegacy()?.expectedChoiceList!!
                    )
            )

    fun buildExclusiveChoiceResult(responseFirstTry: Response?,
                                   responseSecondTry: Response?,
                                   statement: Statement) : PlayerExclusiveChoiceResults =
            PlayerExclusiveChoiceResults(
                    statementType = QuestionType.ExclusiveChoice,
                    explanationFirstTry = if(responseFirstTry != null) ExplanationData(responseFirstTry) else null,
                    explanationSecondTry = if(responseSecondTry != null) ExplanationData(responseSecondTry) else null,
                    choiceFirstTry = responseFirstTry?.learnerChoice,
                    choiceSecondTry = responseSecondTry?.learnerChoice,
                    scoreFirstTry = responseFirstTry?.score?.intValueExact(),
                    scoreSecondTry = responseSecondTry?.score?.intValueExact(),
                    expectedChoice = ExclusiveChoiceSpecification(
                            nbCandidateItem = statement.choiceSpecification?.toLegacy()?.itemCount!!,
                            expectedChoice = statement.choiceSpecification?.toLegacy()?.expectedChoiceList!![0]
                    )
            )

}