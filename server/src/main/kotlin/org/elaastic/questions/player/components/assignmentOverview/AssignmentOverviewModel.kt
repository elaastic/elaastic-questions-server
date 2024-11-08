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

import org.elaastic.questions.assignment.sequence.Sequence

/**
 * Model for the assignment overview page.
 *
 * @param teacher true if the user is a teacher
 * @param nbRegisteredUser the number of registered users
 * @param assignmentTitle the title of the assignment
 * @param courseTitle the title of the course
 * @param courseId the id of the course
 * @param subjectTitle the title of the subject
 * @param subjectId the id of the subject
 * @param audience the audience of the assignment
 * @param assignmentId the id of the assignment
 * @param sequences the list of sequences
 * @param selectedSequenceId the id of the selected sequence
 * @param hideStatementContent true if the statement content should be hidden
 * @param isRevisionMode true if the assignment is in revision mode
 */
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
    val hideStatementContent: Boolean = false,
    val isRevisionMode: Boolean = false,
    val indexOfSelectedSequence: Int,
) {


    val isSingleSequence: Boolean = sequences.size == 1

    /**
     * Information about a sequence.
     * Use to display information about a sequence in the list of sequences in the assignment overview page.
     * Use by the assignmentOverview component in `_assignment-overview.html`
     */
    data class SequenceInfo(
        val id: Long,
        val title: String,
        val content: String,
        val icons: List<PhaseIcon>,
        val hideStatementContent: Boolean,
        val revisionTag: Boolean,
        val nbReportTotal: Int = 0,
        val nbReportToModerate: Int = 0,
    )

    data class PhaseIcon(
        val icon: String,
        val title: String,
    )

}
