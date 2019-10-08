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

package org.elaastic.questions.player

import org.elaastic.questions.player.components.explanationViewer.ChoiceExplanationViewerModel
import org.elaastic.questions.player.components.explanationViewer.ExplanationData
import org.elaastic.questions.player.components.explanationViewer.OpenExplanationViewerModel
import org.elaastic.questions.player.components.explanationViewer.ResponseData
import java.math.BigDecimal

@DslMarker
annotation class ExplanationViewerDsl

object ExplanationViewerCmd {
    fun openQuestionSituation(block: OpenExplanationViewerSituationBuilder.() -> Unit): TestingPlayerController.ExplanationViewerSituation =
            OpenExplanationViewerSituationBuilder().apply(block).build()

    fun choiceQuestionSituation(block: ChoiceExplanationViewerSituationBuilder.() -> Unit): TestingPlayerController.ExplanationViewerSituation =
            ChoiceExplanationViewerSituationBuilder().apply(block).build()
}

@ExplanationViewerDsl
class OpenExplanationViewerSituationBuilder {
    var sequenceId: Long? = null
    var description: String = ""

    private val explanations = mutableListOf<ExplanationData>()

    fun explanations(block: Explanations.() -> Unit) {
        explanations.addAll(Explanations().apply(block))
    }

    fun build() = TestingPlayerController.ExplanationViewerSituation(
            sequenceId = sequenceId!!,
            description = description,
            explanationViewerModel =
            OpenExplanationViewerModel(explanations)
    )
}

@ExplanationViewerDsl
class ChoiceExplanationViewerSituationBuilder {
    var sequenceId: Long? = null
    var description: String = ""

    private val explanationsByResponse = mutableMapOf<ResponseData, List<ExplanationData>>()

    fun explanationsByResponse(block: ExplanationsByResponse.() -> Unit) {
        explanationsByResponse.putAll(ExplanationsByResponse().apply(block))
    }

    fun build() = TestingPlayerController.ExplanationViewerSituation(
            sequenceId = sequenceId!!,
            description = description,
            explanationViewerModel =
            ChoiceExplanationViewerModel(explanationsByResponse)
    )
}

@ExplanationViewerDsl
class ExplanationsByResponse : HashMap<ResponseData, List<ExplanationData>>() {

//    fun explanations(block: ResponseDataToExplanationDatasBuilder.() -> Unit) {
//        plusAssign(ResponseDataToExplanationDatasBuilder().apply(block).build())
//    }


    fun response(choices: List<Int>, score: Int, correct: Boolean, block: ResponseDataToExplanationDatasBuilder.() -> Unit) {
        plusAssign(
                ResponseDataToExplanationDatasBuilder(
                        choices,
                        score,
                        correct
                ).apply(block).build()
        )
    }
}

@ExplanationViewerDsl
class ResponseDataToExplanationDatasBuilder(
        val choices: List<Int>,
        val score: Int = 0,
        val correct: Boolean = false
) {
    private var explanations = mutableListOf<ExplanationData>()

    fun explanation(block: ExplanationDataBuilder.() -> Unit) {
        explanations.add(ExplanationDataBuilder().apply(block).build())
    }

    fun build() = Pair(
            ResponseData(choices, score, correct),
            explanations
    )
}

@ExplanationViewerDsl
class Explanations : ArrayList<ExplanationData>() {
    fun explanation(block: ExplanationDataBuilder.() -> Unit) {
        add(ExplanationDataBuilder().apply(block).build())
    }
}

@ExplanationViewerDsl
class ExplanationDataBuilder {
    var content: String? = null
    var author: String? = null
    var nbEvaluations: Int = 0
    var meanGrade: BigDecimal? = null

    fun build() = ExplanationData(
            content = content,
            author = author,
            nbEvaluations = nbEvaluations,
            meanGrade = meanGrade
    )
}


