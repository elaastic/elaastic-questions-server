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

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseSet
import org.elaastic.questions.assignment.sequence.interaction.results.ResponsesDistributionFactory
import org.elaastic.questions.player.components.explanationViewer.*
import org.elaastic.questions.player.components.responseDistributionChart.ChoiceSpecificationData
import org.elaastic.questions.player.components.responseDistributionChart.ResponseDistributionChartModel

object ResultsModelFactory {

    fun build(sequence: Sequence,
              responseSet: ResponseSet): ResultsModel =
            if (sequence.statement.hasChoices())
                ChoiceResultsModel(
                        sequenceIsStopped = sequence.isStopped(),
                        sequenceId = sequence.id ?: error("This sequence has no ID"),
                        hasExplanations = sequence.getResponseSubmisssionSpecification().studentsProvideExplanation,
                        hasAnyResult = !responseSet.isEmpty(),
                        responseDistributionChartModel =
                        buildResponseDistributionChartModel(sequence, responseSet),
                        explanationViewerModel =
                        if (sequence.getResponseSubmisssionSpecification().studentsProvideExplanation)
                            ExplanationViewerModelFactory.buildChoice(
                                    choiceSpecification = sequence.statement.choiceSpecification!!,
                                    responseList = responseSet[if (sequence.executionIsFaceToFace()) 1 else 2]
                            )
                        else null
                )
            else OpenResultsModel(
                    sequenceIsStopped = sequence.isStopped(),
                    sequenceId = sequence.id ?: error("This sequence has no ID"),
                    explanationViewerModel =
                    ExplanationViewerModelFactory.buildOpen(
                            responseSet[if (sequence.executionIsFaceToFace()) 1 else 2]
                    )
            )

    private fun buildResponseDistributionChartModel(sequence: Sequence,
                                                    responseSet: ResponseSet): ResponseDistributionChartModel =
            sequence.getResponseSubmissionInteraction().let { responseSubmissionInteraction ->
                val choiceSpecification = sequence.statement.choiceSpecification
                        ?: error("This is an open question ; cannot compute response distribution")

                ResponseDistributionChartModel(
                        interactionId = responseSubmissionInteraction.id!!, // TODO would be more relevant to use sequence.id
                        choiceSpecification = ChoiceSpecificationData(choiceSpecification),
                        results = ResponsesDistributionFactory.build(
                                sequence.statement.choiceSpecification!!,
                                responseSet
                        ).toLegacyFormat()
                )
            }


}