package org.elaastic.player.explanations

import org.elaastic.questions.player.components.recommendation.CorrectAndConfidenceDegreeComparator
import org.elaastic.questions.player.components.recommendation.CorrectAndMeanGradeComparator

class ChoiceExplanationViewerModel(
    explanationsByResponse: Map<ResponseData, List<ExplanationData>>,
    val showOnlyCorrectResponse: Boolean = false,
    alreadySorted: Boolean = false,
    override val studentsIdentitiesAreDisplayable: Boolean = false,
    val recommendedExplanationsComparator: Comparator<ExplanationData>? = CorrectAndMeanGradeComparator(),
) : DefaultExplanationViewerModel(explanations = explanationsByResponse.values.flatten(), alreadySorted) {

    override val hasChoice = true

    val explanationsByResponse =
            if (alreadySorted) explanationsByResponse
            else explanationsByResponse.mapValues {
                it.value.sortedWith(
                        compareByDescending<ExplanationData> { it.meanGrade }
                        .thenByDescending { it.nbEvaluations }
                )
            }

    val allResponses: List<ExplanationData> = explanationsByResponse.values.flatten()

    val correctResponse = this.explanationsByResponse.keys.find { it.correct }
            ?: throw IllegalStateException("There is no correct answer")

    val explanationsByIncorrectResponses = this.explanationsByResponse.filter { !it.key.correct }
    val explanationsForIncorrectResponses = explanationsByIncorrectResponses.values.flatten()

    val explanationsForCorrectResponses = this.explanationsByResponse.filter { it.key.correct }.values.flatten()

    val hasExplanationsForIncorrectResponse = this.explanationsByResponse.any { !it.key.correct && it.value.isNotEmpty() }

    override val nbExplanationsForCorrectResponse = explanationsForCorrectResponses.count()

    override val hasRecommendedExplanations = (recommendedExplanationsComparator != null)

    val correctAreRecommended = recommendedExplanationsComparator is CorrectAndMeanGradeComparator || recommendedExplanationsComparator is CorrectAndConfidenceDegreeComparator

    val explanationsByCorrectness = if(correctAreRecommended) explanationsForCorrectResponses else explanationsForIncorrectResponses

    val recommendedExplanations =
         if (recommendedExplanationsComparator != null)
                explanationsByCorrectness.sortedWith(recommendedExplanationsComparator).reversed()
            else
                explanationsForCorrectResponses

    // Get recommended explanations from students which are not hidden
    private val recommendedStudentsExplanations = recommendedExplanations
        .filter { !it.fromTeacher && !it.hiddenByTeacher }

    override val nbRecommendedExplanations = recommendedStudentsExplanations.count {it.recommendedByTeacher }

    override val explanationsExcerpt = getExplanationsExcerpt(
        recommendedStudentsExplanations,
        nbRecommendedExplanations
    )
}