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
package org.elaastic.questions.player.components.draxo

import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoGrading
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGrading
import java.math.BigDecimal

data class DraxoEvaluationModel(
    val graderName: String,
    val graderNum: Int,
    val score: BigDecimal?,
    val draxoEvaluation: DraxoEvaluation,
    val userCanDisplayStudentsIdentity: Boolean = false,
    val draxoPeerGradingId : Long? = null,
    val utilityGrade: UtilityGrade? = null,
) {
    constructor(
        graderIndex: Int,
        draxoPeerGrading: DraxoPeerGrading,
        userCanDisplayStudentsIdentity: Boolean = false
    ) : this(
        draxoPeerGrading.grader.getDisplayName(),
        graderIndex + 1,
        DraxoGrading.computeGrade(draxoPeerGrading),
        draxoPeerGrading.getDraxoEvaluation(),
        userCanDisplayStudentsIdentity,
        draxoPeerGrading.id,
        draxoPeerGrading.utilityGrade
    )
}