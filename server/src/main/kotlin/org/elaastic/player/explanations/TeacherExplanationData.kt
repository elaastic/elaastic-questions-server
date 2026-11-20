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

import org.elaastic.material.instructional.question.legacy.LearnerChoice
import org.elaastic.activity.response.ConfidenceDegree
import java.math.BigDecimal

class TeacherExplanationData(
    responseId: Long,
    content: String? = null,
    author: String? = null,
    nbEvaluations: Int = 0,
    nbDraxoEvaluations: Int = 0,
    meanGrade: BigDecimal? = null,
    confidenceDegree: ConfidenceDegree? = null,
    score: BigDecimal? = null,
    correct: Boolean? = (score?.compareTo(BigDecimal(100)) == 0),
    choiceList: LearnerChoice? = null,
    nbDraxoEvaluationsHidden: Int = 0,
) : ExplanationData(
    responseId,
    content,
    author,
    nbEvaluations,
    nbDraxoEvaluations,
    meanGrade,
    confidenceDegree,
    score,
    correct,
    choiceList,
    nbDraxoEvaluationsHidden = nbDraxoEvaluationsHidden,
) {
    constructor(explanationData: ExplanationData) : this(
        responseId = explanationData.responseId,
        content = explanationData.content,
        author = explanationData.author,
        nbEvaluations = explanationData.nbEvaluations,
        nbDraxoEvaluations = explanationData.nbDraxoEvaluations,
        meanGrade = explanationData.meanGrade,
        confidenceDegree = explanationData.confidenceDegree,
        correct = explanationData.correct,
        score = explanationData.score,
        choiceList = explanationData.choiceList,
        nbDraxoEvaluationsHidden = explanationData.nbDraxoEvaluationsHidden,
    )

    override val fromTeacher = true
}