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

import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import java.math.BigDecimal
import java.math.RoundingMode

open class ExplanationData(
    val responseId: Long,
    val content: String? = null,
    val author: String? = null,
    val nbEvaluations: Int = 0,
    val nbDraxoEvaluations: Int = 0,
    meanGrade: BigDecimal? = null,
    val confidenceDegree: ConfidenceDegree? = null,
    val score: BigDecimal? = null,
    val correct: Boolean? = (score?.compareTo(BigDecimal(100)) == 0),
    val choiceList: LearnerChoice? = null,
    val hiddenByTeacher: Boolean = false,
    val recommendedByTeacher: Boolean = false,
    val nbDraxoEvaluationsHidden: Int = 0,
    val userId: Long? = null,
) {
    constructor(response: Response) : this(
        responseId = response.id!!,
        content = response.explanation,
        author = response.learner.getDisplayName(),
        nbEvaluations = response.evaluationCount,
        nbDraxoEvaluations = response.draxoEvaluationCount,
        meanGrade = response.meanGrade,
        confidenceDegree = response.confidenceDegree,
        correct = response.score?.compareTo(BigDecimal(100)) == 0,
        score = response.score,
        choiceList = response.learnerChoice,
        hiddenByTeacher = response.hiddenByTeacher,
        recommendedByTeacher = response.recommendedByTeacher,
        nbDraxoEvaluationsHidden = response.draxoEvaluationHiddenCount,
        userId = response.learner.id
    )

    val meanGrade = meanGrade
        ?.setScale(2, RoundingMode.CEILING)
        ?.stripTrailingZeros()

    open val fromTeacher = false

    /**
     * Number of evaluation visible to the user
     * A student can only see the evaluations if they are not hidden by the teacher
     * @param isTeacher true if the user is a teacher false otherwise
     * @return the number of evaluations
     */
    fun getNbEvaluation(isTeacher: Boolean): Int {
        return if (isTeacher) nbEvaluations else nbEvaluations - nbDraxoEvaluationsHidden
    }

    /**
     * Number of Draxo evaluation visible to the user
     * A student can only see the Draxo evaluations if they are not hidden by the teacher
     * @param isTeacher true if the user is a teacher false otherwise
     * @return the number of Draxo evaluations
     */
    fun getNbDraxoEvaluation(isTeacher: Boolean): Int {
        return if (isTeacher) nbDraxoEvaluations else nbDraxoEvaluations - nbDraxoEvaluationsHidden
    }
}
