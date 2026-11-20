/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.player.results.learner

import org.elaastic.player.explanations.ExplanationDataFactory
import org.elaastic.material.instructional.question.ExclusiveChoiceSpecification
import org.elaastic.material.instructional.question.MultipleChoiceSpecification
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.activity.response.Response

object LearnerResultsModelFactory {

    fun buildOpenResult(responseFirstTry: Response?,
                        responseSecondTry: Response?,
                        responseFirstTryHasChatGPTEvaluation: Boolean,
                        responseSecondTryHasChatGPTEvaluation: Boolean,
                        ) : LearnerOpenResults =
        LearnerOpenResults(
            explanationFirstTry = if (responseFirstTry != null) ExplanationDataFactory.create(
                responseFirstTry,
                responseFirstTryHasChatGPTEvaluation
            ) else null,
            explanationSecondTry = if (responseSecondTry != null) ExplanationDataFactory.create(
                responseSecondTry,
                responseSecondTryHasChatGPTEvaluation
            ) else null
        )

    fun buildMultipleChoiceResult(responseFirstTry: Response?,
                                  responseSecondTry: Response?,
                                  responseFirstTryHasChatGPTEvaluation: Boolean,
                                  responseSecondTryHasChatGPTEvaluation: Boolean,
                                  statement: Statement
    ) : LearnerMultipleChoiceResults =
        LearnerMultipleChoiceResults(
            explanationFirstTry = if (responseFirstTry != null) ExplanationDataFactory.create(
                responseFirstTry,
                responseFirstTryHasChatGPTEvaluation
            ) else null,
            explanationSecondTry = if (responseSecondTry != null) ExplanationDataFactory.create(
                responseSecondTry,
                responseSecondTryHasChatGPTEvaluation
            ) else null,
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
                                   statement: Statement
    ) : LearnerExclusiveChoiceResults =
        LearnerExclusiveChoiceResults(
            explanationFirstTry = if (responseFirstTry != null) ExplanationDataFactory.create(
                responseFirstTry,
                responseFirstTryHasChatGPTEvaluation
            ) else null,
            explanationSecondTry = if (responseSecondTry != null) ExplanationDataFactory.create(
                responseSecondTry,
                responseSecondTryHasChatGPTEvaluation
            ) else null,
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