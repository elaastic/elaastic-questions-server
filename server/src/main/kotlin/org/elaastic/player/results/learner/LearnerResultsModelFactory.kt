/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

import org.elaastic.activity.response.Response
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationResponseStore
import org.elaastic.material.instructional.question.ExclusiveChoiceSpecification
import org.elaastic.material.instructional.question.MultipleChoiceSpecification
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.player.explanations.ExplanationDataFactory

object LearnerResultsModelFactory {

    /**
     * Get the LearnerResultsModel for the given responses and statement
     *
     * @param responseFirstAttempt the response of the learner for the first attempt
     * @param responseSecondAttempt the response of the learner for the second attempt
     * @param statement the statement of the sequence
     */
    fun buildLearnerResultsModel(
        learnerSequenceResponses: LearnerSequenceResponses,
        statement: Statement
    ): LearnerResultsModel {
        val learnerResultsModel = when (statement.questionType) {
            QuestionType.ExclusiveChoice -> buildExclusiveChoiceResult(
                learnerSequenceResponses,
                statement
            )

            QuestionType.MultipleChoice -> buildMultipleChoiceResult(
                learnerSequenceResponses,
                statement
            )

            QuestionType.OpenEnded -> buildOpenResult(
                learnerSequenceResponses,
            )
        }
        return learnerResultsModel
    }

    fun buildOpenResult(
        learnerSequenceResponses: LearnerSequenceResponses
    ): LearnerOpenResults = with(learnerSequenceResponses) {
        LearnerOpenResults(
            explanationFirstTry = if (responseFirstAttempt != null) ExplanationDataFactory.create(
                responseFirstAttempt,
                responseFirstTryHasChatGPTEvaluation
            ) else null,
            explanationSecondTry = if (responseSecondAttempt != null) ExplanationDataFactory.create(
                responseSecondAttempt,
                responseSecondTryHasChatGPTEvaluation
            ) else null
        )
    }

    fun buildMultipleChoiceResult(
        learnerSequenceResponses: LearnerSequenceResponses,
        statement: Statement
    ): LearnerMultipleChoiceResults = with(learnerSequenceResponses) {
        LearnerMultipleChoiceResults(
            explanationFirstTry = if (responseFirstAttempt != null) ExplanationDataFactory.create(
                responseFirstAttempt,
                responseFirstTryHasChatGPTEvaluation
            ) else null,
            explanationSecondTry = if (responseSecondAttempt != null) ExplanationDataFactory.create(
                responseSecondAttempt,
                responseSecondTryHasChatGPTEvaluation
            ) else null,
            choiceFirstTry = responseFirstAttempt?.learnerChoice,
            choiceSecondTry = responseSecondAttempt?.learnerChoice,
            scoreFirstTry = responseFirstAttempt?.score?.intValueExact(),
            scoreSecondTry = responseSecondAttempt?.score?.intValueExact(),
            expectedChoice = MultipleChoiceSpecification(
                nbCandidateItem = statement.choiceSpecification?.toLegacy()?.itemCount!!,
                expectedChoiceList = statement.choiceSpecification?.toLegacy()?.expectedChoiceList!!
            )
        )
    }

    fun buildExclusiveChoiceResult(
        learnerSequenceResponses: LearnerSequenceResponses,
        statement: Statement
    ): LearnerExclusiveChoiceResults = with(learnerSequenceResponses) {
        LearnerExclusiveChoiceResults(
            explanationFirstTry = if (responseFirstAttempt != null) ExplanationDataFactory.create(
                responseFirstAttempt,
                responseFirstTryHasChatGPTEvaluation
            ) else null,
            explanationSecondTry = if (responseSecondAttempt != null) ExplanationDataFactory.create(
                responseSecondAttempt,
                responseSecondTryHasChatGPTEvaluation
            ) else null,
            choiceFirstTry = responseFirstAttempt?.learnerChoice,
            choiceSecondTry = responseSecondAttempt?.learnerChoice,
            scoreFirstTry = responseFirstAttempt?.score?.intValueExact(),
            scoreSecondTry = responseSecondAttempt?.score?.intValueExact(),
            expectedChoice = ExclusiveChoiceSpecification(
                nbCandidateItem = statement.choiceSpecification?.toLegacy()?.itemCount!!,
                expectedChoice = statement.choiceSpecification?.toLegacy()?.expectedChoiceList!![0]
            )
        )
    }
}

data class LearnerSequenceResponses(
    val responseFirstAttempt: Response?,
    val responseSecondAttempt: Response?,
    val chatGptEvaluationResponseStore: ChatGptEvaluationResponseStore
) {
    val responseFirstTryHasChatGPTEvaluation: Boolean
        get() = responseFirstAttempt != null && chatGptEvaluationResponseStore.responseHasBeenEvaluatedByChatGpt(
            responseFirstAttempt.id!!
        )

    val responseSecondTryHasChatGPTEvaluation: Boolean
        get() = responseSecondAttempt != null && chatGptEvaluationResponseStore.responseHasBeenEvaluatedByChatGpt(
            responseSecondAttempt.id!!
        )
}