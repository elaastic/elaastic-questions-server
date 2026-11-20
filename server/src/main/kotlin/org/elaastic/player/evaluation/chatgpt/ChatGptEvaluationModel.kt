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

package org.elaastic.player.evaluation.chatgpt

import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationStatus
import org.elaastic.moderation.ReportCandidate
import org.elaastic.moderation.ReportReason
import org.elaastic.moderation.UtilityGrade
import java.math.BigDecimal

data class ChatGptEvaluationModel(
    val evaluationId: Long? = null,
    val annotation: String? = null,
    val grade: BigDecimal? = null,
    val status: String? = ChatGptEvaluationStatus.UNKNOWN.name,
    val sequenceId: Long,
    val utilityGradeValues: Array<UtilityGrade> = UtilityGrade.values(),
    val reportValues: Array<ReportReason> = ReportReason.values(),
    var viewedByTeacher: Boolean = false,
    override var utilityGrade: UtilityGrade? = null,
    override var hiddenByTeacher: Boolean = false,
    override var removedByTeacher: Boolean = false,
    override var reportReasons: String? = null,
    override var reportComment: String? = null,
    val gradingID: Long? = null,
    val canHideGrading: Boolean = false,
    val responseId: Long? = null,
    override var teacherUtilityGrade: UtilityGrade? = null,
    ) : ReportCandidate {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatGptEvaluationModel

        return evaluationId == other.evaluationId
    }

    override fun hashCode(): Int {
        return evaluationId?.hashCode() ?: 0
    }

    /**
     * Check if the student has reported this evaluation
     *
     * @return true if the student has reported this evaluation, false
     *    otherwise
     */
    fun isReported() = reportReasons.isNullOrBlank().not()
}
