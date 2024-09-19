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

class OpenExplanationViewerModel(
    explanations: List<ExplanationData>,
    alreadySorted: Boolean = false,
    override val studentsIdentitiesAreDisplayable: Boolean = false
) : ExplanationViewerModel {
    val explanations =
        if (alreadySorted) explanations
        else explanations.sortedWith(
            compareByDescending<ExplanationData> { it.meanGrade }.thenByDescending { it.nbEvaluations }
        )
    override val hasChoice = false
    override val nbExplanations = this.explanations.count()
    val recommendedStudentsExplanations = this.explanations
        .filter { !it.fromTeacher && !it.hiddenByTeacher }

    override val nbRecommendedExplanations = recommendedStudentsExplanations.count { it.recommendedByTeacher }

    override val explanationsExcerpt =
        if (nbRecommendedExplanations > 3) {
            recommendedStudentsExplanations.filter { it.recommendedByTeacher }
        } else {
            recommendedStudentsExplanations
                .sortedWith(
                    compareByDescending<ExplanationData> { it.recommendedByTeacher }
                    .thenByDescending { it.meanGrade }
                    .thenByDescending { it.nbEvaluations }
                ).take(3)
        }
    val nbExplanationsForCorrectResponse = nbExplanations
    override val hasMoreThanExcerpt = nbExplanations > 3
    override val hasHiddenByTeacherExplanations = this.explanations.any { it.hiddenByTeacher }
    val hasRecommendedExplanations = false
    override val teacherExplanation =
        this.explanations.firstOrNull { it is TeacherExplanationData } as TeacherExplanationData?

}