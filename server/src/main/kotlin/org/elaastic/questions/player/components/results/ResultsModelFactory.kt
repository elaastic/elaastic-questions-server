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
package org.elaastic.questions.player.components.results

import ConfidenceDistributionChartModel
import EvaluationDistributionChartModel
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseSet
import org.elaastic.questions.assignment.sequence.interaction.results.ConfidenceDistributionFactory
import org.elaastic.questions.assignment.sequence.interaction.results.GradingDistributionFactory
import org.elaastic.questions.assignment.sequence.interaction.results.ResponsesDistributionFactory
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.common.web.MessageBuilder
import org.elaastic.common.abtesting.ElaasticFeatures
import org.elaastic.questions.player.components.explanationViewer.*
import org.elaastic.questions.player.components.recommendation.RecommendationResolver
import org.elaastic.questions.player.components.responseDistributionChart.ChoiceSpecificationData
import org.elaastic.questions.player.components.responseDistributionChart.ResponseDistributionChartModel
import org.togglz.core.manager.FeatureManager
import org.togglz.core.Feature

object ResultsModelFactory {

    fun build(
        teacher: Boolean,
        sequence: Sequence,
        featureManager: FeatureManager,
        responseSet: ResponseSet,
        userCanRefreshResults: Boolean,
        messageBuilder: MessageBuilder,
        peerGradings: List<PeerGrading>? = null,
        explanationHasChatGPTEvaluationMap: Map<Long, Boolean>
    ): ResultsModel =
        if (sequence.statement.hasChoices()) {
            val recommendationIsActive = featureManager.isActive(Feature { ElaasticFeatures.RECOMMENDATIONS.name })
            val recommendationModel = if (recommendationIsActive)
                RecommendationResolver.resolve(
                    responseSet,
                    peerGradings,
                    sequence,
                    messageBuilder
                ) else null
            ChoiceResultsModel(
                sequenceIsStopped = sequence.isStopped(),
                sequenceId = sequence.id ?: error("This sequence has no ID"),
                responseDistributionChartModel = buildResponseDistributionChartModel(sequence, responseSet),
                confidenceDistributionChartModel = buildConfidenceDistributionChartModel(sequence, responseSet),
                evaluationDistributionChartModel = buildEvaluationDistributionChartModel(sequence, peerGradings),
                recommendationModel = recommendationModel,
                explanationViewerModel = createChoiceExplanationViewerModel(
                    teacher,
                    sequence,
                    responseSet,
                    recommendationModel?.recommendedExplanationsComparator,
                    explanationHasChatGPTEvaluationMap
                ),
                userCanRefreshResults = userCanRefreshResults,
                userCanDisplayStudentsIdentity = teacher
            )
        } else OpenResultsModel(
            sequenceIsStopped = sequence.isStopped(),
            sequenceId = sequence.id ?: error("This sequence has no ID"),
            explanationViewerModel = ExplanationViewerModelFactory.buildOpen(
                teacher,
                responseSet[sequence.whichAttemptEvaluate()],
                explanationHasChatGPTEvaluationMap
            ),
            userCanRefreshResults = userCanRefreshResults,
            userCanDisplayStudentsIdentity = teacher
        )

    private fun buildResponseDistributionChartModel(
        sequence: Sequence,
        responseSet: ResponseSet
    ): ResponseDistributionChartModel =
        sequence.getResponseSubmissionInteraction().let { responseSubmissionInteraction ->
            val choiceSpecification = sequence.statement.choiceSpecification
                ?: error("This is an open question ; cannot compute response distribution")

            ResponseDistributionChartModel(
                interactionId = responseSubmissionInteraction.id!!, // TODO (+) would be more relevant to use sequence.id
                choiceSpecification = ChoiceSpecificationData(choiceSpecification),
                results = ResponsesDistributionFactory.build(
                    sequence.statement.choiceSpecification!!,
                    responseSet
                ).toLegacyFormat()
            )
        }

    private fun buildConfidenceDistributionChartModel(
        sequence: Sequence,
        responseSet: ResponseSet
    ): ConfidenceDistributionChartModel =
        sequence.getResponseSubmissionInteraction().let { responseSubmissionInteraction ->
            val choiceSpecification = sequence.statement.choiceSpecification
                ?: error("This is an open question ; cannot compute confidence distribution")

            ConfidenceDistributionChartModel(
                interactionId = responseSubmissionInteraction.id!!, // TODO (+) would be more relevant to use sequence.id
                choiceSpecification = ChoiceSpecificationData(choiceSpecification),
                results = ConfidenceDistributionFactory.build(
                    sequence.statement.choiceSpecification!!,
                    responseSet
                ).toJSON()
            )
        }

    private fun buildEvaluationDistributionChartModel(
        sequence: Sequence,
                                                      peerGradings: List<PeerGrading>?): EvaluationDistributionChartModel =
        sequence.getResponseSubmissionInteraction().let { responseSubmissionInteraction ->
            val choiceSpecification = sequence.statement.choiceSpecification
                ?: error("This is an open question ; cannot compute confidence distribution")

            EvaluationDistributionChartModel(
                interactionId = responseSubmissionInteraction.id!!, // TODO (+) would be more relevant to use sequence.id
                choiceSpecification = ChoiceSpecificationData(choiceSpecification),
                results = GradingDistributionFactory.build(
                    sequence.statement.choiceSpecification!!,
                    peerGradings
                ).toLegacyFormat()
            )
        }

    private fun createChoiceExplanationViewerModel(
        teacher: Boolean,
        sequence: Sequence,
        responseSet: ResponseSet,
        recommendedExplanations: Comparator<ExplanationData>? = null,
        explanationHasChatGPTEvaluationMap: Map<Long, Boolean>
    ) =
        if (sequence.getResponseSubmissionSpecification().studentsProvideExplanation)
            ExplanationViewerModelFactory.buildChoice(
                teacher = teacher,
                choiceSpecification = sequence.statement.choiceSpecification!!,
                responseList = responseSet[sequence.whichAttemptEvaluate()],
                recommendedExplanationsComparator = recommendedExplanations,
                explanationHasChatGPTEvaluationMap = explanationHasChatGPTEvaluationMap,
            )
        else null

}
