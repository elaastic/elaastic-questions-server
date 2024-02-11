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

package org.elaastic.questions.player.components.assignmentOverview

data class AssignmentOverviewModel(
        val teacher: Boolean,
        val nbRegisteredUser: Int,
        val assignmentTitle: String,
        val courseTitle: String?,
        val courseId: Long?,
        val subjectTitle: String?,
        val subjectId: Long?,
        val audience: String?,
        val assignmentId: Long,
        val sequences: List<SequenceInfo>,
        val selectedSequenceId: Long? = null,
        val hideStatementContent: Boolean = false
) {

    data class SequenceInfo(
            val id: Long,
            val title: String,
            val content: String,
            val icons: List<PhaseIcon>,
            val hideStatementContent: Boolean,
            val revisionTag: Boolean,
            val contextInformation: ContextInformation
    )

    data class PhaseIcon(
            val icon: String,
            val title: String,
    )

    data class ContextInformation(
            val hasStarted: Boolean,
            val context: String,
            val evaluationType: String,
            val nbEvaluation: String,
            val revisionMode: String
    )
}
