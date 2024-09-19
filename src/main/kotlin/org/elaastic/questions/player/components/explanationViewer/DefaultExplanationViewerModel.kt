package org.elaastic.questions.player.components.explanationViewer

abstract class DefaultExplanationViewerModel(
    explanations: List<ExplanationData>,
    alreadySorted: Boolean = false,
) : ExplanationViewerModel {
    val nbOfExplanationExcerptToDisplay = 3

    val explanations =
        if (alreadySorted) explanations
        else explanations.sortedWith(
            compareByDescending<ExplanationData> { it.meanGrade }.thenByDescending { it.nbEvaluations }
        )

    override val nbExplanations = explanations.count()

    override val hasMoreThanExcerpt = nbExplanations > nbOfExplanationExcerptToDisplay

    override val hasHiddenByTeacherExplanations = explanations.any { it.hiddenByTeacher }

    override val teacherExplanation =
        explanations.firstOrNull { it is TeacherExplanationData } as TeacherExplanationData?

    private val recommendedStudentsExplanations = explanations
        .filter { !it.fromTeacher && !it.hiddenByTeacher }

    override val nbRecommendedExplanations = recommendedStudentsExplanations.count { it.recommendedByTeacher }

    override val explanationsExcerpt = getExplanationsExcerpt(
        recommendedStudentsExplanations,
        nbRecommendedExplanations
    )

    fun getExplanationsExcerpt(
        recommendedStudentsExplanations: List<ExplanationData>,
        nbRecommendedExplanation: Int,
    ): List<ExplanationData> {
        return if (nbRecommendedExplanation >= nbOfExplanationExcerptToDisplay) {
            recommendedStudentsExplanations.filter { it.recommendedByTeacher }
        } else {
            recommendedStudentsExplanations
                .sortedWith(
                    compareByDescending<ExplanationData> { it.recommendedByTeacher }
                        .thenByDescending { it.meanGrade }
                        .thenByDescending { it.nbEvaluations }
                ).take(nbOfExplanationExcerptToDisplay)
        }
    }

    open val hasRecommendedExplanations = false
    open val nbExplanationsForCorrectResponse = nbExplanations
}