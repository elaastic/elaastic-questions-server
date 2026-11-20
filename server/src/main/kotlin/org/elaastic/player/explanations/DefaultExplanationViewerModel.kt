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

package org.elaastic.player.explanations

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