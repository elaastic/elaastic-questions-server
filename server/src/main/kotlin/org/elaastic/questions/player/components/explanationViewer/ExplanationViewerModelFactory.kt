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
package org.elaastic.questions.player.components.explanationViewer

import org.elaastic.questions.assignment.choice.ChoiceSpecification
import org.elaastic.sequence.interaction.response.Response

object ExplanationViewerModelFactory {

    fun buildOpen(
        teacher: Boolean,
        responseList: List<Response>,
        explanationHasChatGPTEvaluationMap: Map<Long, Boolean>
    ) =
        OpenExplanationViewerModel(
            responseList.map { ExplanationDataFactory.create(it,explanationHasChatGPTEvaluationMap[it.id] == true) },
            true,
            studentsIdentitiesAreDisplayable = teacher
        )

    fun buildChoice(
        teacher: Boolean,
        responseList: List<Response>,
        choiceSpecification: ChoiceSpecification,
        recommendedExplanationsComparator: Comparator<ExplanationData>? = null,
        explanationHasChatGPTEvaluationMap: Map<Long, Boolean>
    ): ExplanationViewerModel =
        ChoiceExplanationViewerModel(
            // TODO I should simplify (merge ChoiceExplanationViewerModel & ChoiceExplanationStore)
            explanationsByResponse = ChoiceExplanationStore(
                choiceSpecification,
                responseList,
                explanationHasChatGPTEvaluationMap,
            ),
            alreadySorted = true,
            studentsIdentitiesAreDisplayable = teacher,
            showOnlyCorrectResponse = !teacher,
            recommendedExplanationsComparator = recommendedExplanationsComparator
        )

}