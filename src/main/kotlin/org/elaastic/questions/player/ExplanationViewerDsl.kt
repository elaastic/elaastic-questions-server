package org.elaastic.questions.player

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

    private val explanations = mutableListOf<PlayerController.ExplanationData>()

    fun explanations(block: Explanations.() -> Unit) {
        explanations.addAll(Explanations().apply(block))
    }

    fun build() = TestingPlayerController.ExplanationViewerSituation(
            sequenceId = sequenceId!!,
            description = description,
            explanationViewerModel =
            PlayerController.OpenExplanationViewerModel(explanations)
    )
}

@ExplanationViewerDsl
class ChoiceExplanationViewerSituationBuilder {
    var sequenceId: Long? = null
    var description: String = ""

    private val explanationsByResponse = mutableMapOf<PlayerController.ResponseData, List<PlayerController.ExplanationData>>()

    fun explanationsByResponse(block: ExplanationsByResponse.() -> Unit) {
        explanationsByResponse.putAll(ExplanationsByResponse().apply(block))
    }

    fun build() = TestingPlayerController.ExplanationViewerSituation(
            sequenceId = sequenceId!!,
            description = description,
            explanationViewerModel =
            PlayerController.ChoiceExplanationViewerModel(explanationsByResponse)
    )
}

@ExplanationViewerDsl
class ExplanationsByResponse : HashMap<PlayerController.ResponseData, List<PlayerController.ExplanationData>>() {

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
    private var explanations = mutableListOf<PlayerController.ExplanationData>()

    fun explanation(block: ExplanationDataBuilder.() -> Unit) {
        explanations.add(ExplanationDataBuilder().apply(block).build())
    }

    fun build() = Pair(
            PlayerController.ResponseData(choices, score, correct),
            explanations
    )
}

@ExplanationViewerDsl
class Explanations : ArrayList<PlayerController.ExplanationData>() {
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

    fun build() = PlayerController.ExplanationData(
            content = content,
            author = author,
            nbEvaluations = nbEvaluations,
            meanGrade = meanGrade
    )
}


