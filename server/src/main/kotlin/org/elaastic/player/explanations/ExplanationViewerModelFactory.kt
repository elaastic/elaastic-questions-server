package org.elaastic.player.explanations

import org.elaastic.questions.assignment.choice.ChoiceSpecification
import org.elaastic.activity.response.Response

object ExplanationViewerModelFactory {

    fun buildOpen(
        teacher: Boolean,
        responseList: List<Response>,
        explanationHasChatGPTEvaluationMap: Map<Long, Boolean>
    ) =
        OpenExplanationViewerModel(
            responseList.map { ExplanationDataFactory.create(it, explanationHasChatGPTEvaluationMap[it.id] == true) },
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