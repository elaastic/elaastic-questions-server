package org.elaastic.questions.player.components.studentResults

import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.player.components.explanationViewer.ExplanationDataFactory
import org.elaastic.questions.subject.statement.Statement

object LearnerResultsModelFactory {

    fun buildOpenResult(responseFirstTry: Response?,
                        responseSecondTry: Response?,
                        responseFirstTryHasChatGPTEvaluation: Boolean,
                        responseSecondTryHasChatGPTEvaluation: Boolean,
                        ) : LearnerOpenResults =
            LearnerOpenResults(
                    explanationFirstTry = if(responseFirstTry != null) ExplanationDataFactory.create(responseFirstTry, responseFirstTryHasChatGPTEvaluation) else null,
                    explanationSecondTry = if(responseSecondTry != null) ExplanationDataFactory.create(responseSecondTry, responseSecondTryHasChatGPTEvaluation) else null
            )

    fun buildMultipleChoiceResult(responseFirstTry: Response?,
                                  responseSecondTry: Response?,
                        responseFirstTryHasChatGPTEvaluation: Boolean,
                        responseSecondTryHasChatGPTEvaluation: Boolean,
                                  statement: Statement) : LearnerMultipleChoiceResults =
            LearnerMultipleChoiceResults(
                    explanationFirstTry = if(responseFirstTry != null) ExplanationDataFactory.create(responseFirstTry, responseFirstTryHasChatGPTEvaluation) else null,
                    explanationSecondTry = if(responseSecondTry != null) ExplanationDataFactory.create(responseSecondTry, responseSecondTryHasChatGPTEvaluation) else null,
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
                        responseFirstTryHasChatGPTEvaluation: Boolean,
                        responseSecondTryHasChatGPTEvaluation: Boolean,
                                   statement: Statement) : LearnerExclusiveChoiceResults =
            LearnerExclusiveChoiceResults(
                    explanationFirstTry = if(responseFirstTry != null) ExplanationDataFactory.create(responseFirstTry, responseFirstTryHasChatGPTEvaluation) else null,
                    explanationSecondTry = if(responseSecondTry != null) ExplanationDataFactory.create(responseSecondTry, responseSecondTryHasChatGPTEvaluation) else null,
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