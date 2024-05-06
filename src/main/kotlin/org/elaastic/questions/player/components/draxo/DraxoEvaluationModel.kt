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
import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId
import java.math.BigDecimal

data class DraxoEvaluationModel(
    val graderName: String,
    val graderNum: Int,
    val score: BigDecimal?,
    val draxoEvaluation: DraxoEvaluation,
    val userCanDisplayStudentsIdentity: Boolean = false,
    val draxoPeerGradingId: Long? = null,
    val utilityGrade: UtilityGrade? = null,
    val hiddenByTeacher: Boolean = false,
    val responseId: Long? = null,
    private val draxoPeerGrading: DraxoPeerGrading? = null
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
        draxoPeerGrading.utilityGrade,
        draxoPeerGrading.hiddenByTeacher,
        draxoPeerGrading.response.id,
        draxoPeerGrading
    )

    /**
     * Check if the given utility grade is the one selected
     *
     * @param utilityGrade the utility grade to check
     * @return true if the given utility grade is selected, false otherwise
     */
    fun isUtilityGradeSelected(utilityGrade: UtilityGrade) = this.utilityGrade == utilityGrade

    /**
     * Check if the student has reported this peer grading
     * @return true if the student has reported this peer grading, false otherwise
     */
    fun isReported() = draxoPeerGrading?.reportReasons.isNullOrBlank().not()

    /**
     * Check if this peer grading can be reacted by a student
     *
     * A peer grading can be reacted if:
     * - it is not hidden by the teacher
     * - it is not reported
     * - the selected option different from DONT_KNOW and NO_OPINION
     * @return true if the student can react to this peer grading, false otherwise
     */
    fun canBeReacted(): Boolean {
        // We want to check if the last-evaluated critéria is not `DONT_KNOW` or `NO_OPINION`
        // If the `currentCriteria` is null, it means that all the criteria have been evaluated,
        // and we want the last one.
        // For DRAXO, the last criteria is the `O` criteria.
        val currentCriteria: Criteria = this.draxoEvaluation.currentCriteria ?: Criteria.O
        val optionIdSelected: OptionId? = this.draxoEvaluation[currentCriteria]
        return !this.hiddenByTeacher && !this.isReported() && (optionIdSelected != OptionId.DONT_KNOW && optionIdSelected != OptionId.NO_OPINION)
    }
}