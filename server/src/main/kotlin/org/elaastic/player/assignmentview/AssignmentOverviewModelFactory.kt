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

package org.elaastic.player.assignmentview

import org.elaastic.assignment.Assignment
import org.elaastic.assignment.ReadyForConsolidation
import org.elaastic.sequence.ExecutionContext
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionType

object AssignmentOverviewModelFactory {

    val chartIcon = AssignmentOverviewModel.PhaseIcon("big grey bar chart outline", "assignment.overview.chartIcon")
    val lockIcon = AssignmentOverviewModel.PhaseIcon("big grey lock", "assignment.overview.lockIcon")
    val minusIcon = AssignmentOverviewModel.PhaseIcon("big grey minus", "assignment.overview.minusIcon")
    val commentIcon = AssignmentOverviewModel.PhaseIcon("big grey comment outline", "assignment.overview.commentIcon")
    val commentsIcon =
        AssignmentOverviewModel.PhaseIcon("big grey comments outline", "assignment.overview.commentsIcon")

    fun build(
        teacher: Boolean,
        assignment: Assignment,
        nbRegisteredUser: Int,
        sequenceToUserActiveInteraction: Map<Sequence, Interaction?>,
        selectedSequenceId: Long? = null,
        nbReportBySequence: Map<Sequence, Pair<Int, Int>> = emptyMap(),
    ): AssignmentOverviewModel = AssignmentOverviewModel(
        teacher = teacher,
        nbRegisteredUser = nbRegisteredUser,
        assignmentTitle = assignment.title,
        courseTitle = if (teacher) assignment.subject?.course?.title else null,
        courseId = if (teacher) assignment.subject?.course?.id else null,
        subjectTitle = if (teacher) assignment.subject!!.title else null,
        subjectId = if (teacher) assignment.subject!!.id else null,
        audience = if (teacher) getAudience(assignment) else null,
        assignmentId = assignment.id!!,
        sequences = assignment.sequences.map {
            getSequenceInfo(
                it,
                teacher,
                assignment,
                nbReportBySequence[it] ?: Pair(0, 0)
            )
        },
        selectedSequenceId = selectedSequenceId,
        isRevisionMode = assignment.readyForConsolidation != ReadyForConsolidation.NotAtAll,
        indexOfSelectedSequence = assignment.sequences.indexOfFirst { it.id == selectedSequenceId }
    )

    fun resolveIcons(
        teacher: Boolean,
        sequence: Sequence,
    ): List<AssignmentOverviewModel.PhaseIcon> =
        resolveIcons(
            teacher,
            sequence.executionContext,
            sequence.state,
            sequence.resultsArePublished,
            sequence.activeInteractionType,
        )

    fun resolveIcons(
        teacher: Boolean,
        executionContext: ExecutionContext,
        state: State,
        resultsArePublished: Boolean,
        activeInteractionType: InteractionType?,
    ): List<AssignmentOverviewModel.PhaseIcon> =
        if (executionContext == ExecutionContext.FaceToFace || !teacher) {
            if (state == State.afterStop) {
                if (resultsArePublished) {
                    listOf(chartIcon)
                } else {
                    listOf(lockIcon)
                }
            } else {
                when (activeInteractionType) {
                    null -> listOf(minusIcon)
                    InteractionType.ResponseSubmission -> listOf(commentIcon)
                    InteractionType.Evaluation -> listOf(commentsIcon)
                    InteractionType.Read -> listOf(chartIcon)
                }
            }
        } else { // Distance & blended for teacher
            when (state) {
                State.beforeStart -> listOf(minusIcon)
                State.afterStop ->
                    if (resultsArePublished) listOf(chartIcon)
                    else listOf(lockIcon)

                else ->
                    if (resultsArePublished) listOf(commentIcon, commentsIcon, chartIcon)
                    else listOf(commentIcon, commentsIcon)
            }
        }

    private fun resolveRevisionTag(
        sequence: Sequence,
        readyForConsolidation: ReadyForConsolidation
    ): Boolean = when (readyForConsolidation) {
        ReadyForConsolidation.NotAtAll -> false
        ReadyForConsolidation.Immediately -> true
        ReadyForConsolidation.AfterTeachings -> sequence.resultsArePublished && (sequence.executionIsFaceToFace() || sequence.isStopped())
    }

    /** Build a [AssignmentOverviewModel] for only one [Sequence]. */
    fun buildOnSequence(
        teacher: Boolean,
        assignment: Assignment,
        nbRegisteredUser: Int,
        userActiveInteraction: Interaction?,
        selectedSequence: Sequence
    ): AssignmentOverviewModel = AssignmentOverviewModel(
        teacher = teacher,
        nbRegisteredUser = nbRegisteredUser,
        assignmentTitle = assignment.title,
        courseTitle = if (teacher) assignment.subject?.course?.title else null,
        courseId = if (teacher) assignment.subject?.course?.id else null,
        subjectTitle = if (teacher) assignment.subject!!.title else null,
        subjectId = if (teacher) assignment.subject!!.id else null,
        audience = if (teacher) getAudience(assignment) else null,
        assignmentId = assignment.id!!,
        sequences = listOf(
            getSequenceInfo(
                selectedSequence,
                teacher,
                assignment,
                Pair(0, 0)
            )
        ),
        selectedSequenceId = selectedSequence.id,
        isRevisionMode = assignment.readyForConsolidation != ReadyForConsolidation.NotAtAll,
        indexOfSelectedSequence = assignment.sequences.indexOfFirst { it.id == selectedSequence.id }
    )

    /**
     * Get the audience of an [Assignment] with the scholar year if it is not
     * null.
     */
    private fun getAudience(assignment: Assignment): String {
        val scholarYear = if (assignment.scholarYear != null) " (${assignment.scholarYear})" else ""
        return "${assignment.audience}$scholarYear"
    }

    /**
     * Get the [AssignmentOverviewModel.SequenceInfo] for a [Sequence].
     *
     * @param sequence the [Sequence] to get the info from.
     * @param isTeacher true if the user is a teacher and false otherwise.
     * @param activeInteraction the active [Interaction] of the [Sequence].
     * @param assignment the [Assignment] of the [Sequence].
     * @param nbReport the number of reports for the [Sequence]. (total, to
     *    moderate)
     */
    private fun getSequenceInfo(
        sequence: Sequence,
        isTeacher: Boolean,
        assignment: Assignment,
        nbReport: Pair<Int, Int>
    ) = AssignmentOverviewModel.SequenceInfo(
        id = sequence.id!!,
        title = sequence.statement.title,
        content = sequence.statement.content,
        hideStatementContent = !isTeacher && sequence.state == State.beforeStart,
        icons = resolveIcons(isTeacher, sequence),
        revisionTag = resolveRevisionTag(sequence, assignment.readyForConsolidation),
        nbReportTotal = nbReport.first,
        nbReportToModerate = nbReport.second
    )
}
