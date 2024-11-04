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

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.RevisionMode
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionType

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
        audience = if (teacher) {
            assignment.audience +
                    if (assignment.scholarYear != null) {
                        " (${assignment.scholarYear})"
                    } else ""
        } else null,
        assignmentId = assignment.id!!,
        sequences = assignment.sequences.map {
            AssignmentOverviewModel.SequenceInfo(
                id = it.id!!,
                title = it.statement.title,
                content = it.statement.content,
                hideStatementContent = !teacher && it.state == State.beforeStart,
                icons = resolveIcons(
                    teacher,
                    it,
                    sequenceToUserActiveInteraction[it]
                ),
                revisionTag = resolveRevisionTag(it, assignment.revisionMode),
                nbReportTotal = nbReportBySequence[it]?.first ?: 0,
                nbReportToModerate = nbReportBySequence[it]?.second ?: 0
            )
        },
        selectedSequenceId = selectedSequenceId,
        isRevisionMode = assignment.revisionMode != RevisionMode.NotAtAll,
        indexOfSelectedSequence = assignment.sequences.indexOfFirst { it.id == selectedSequenceId }
    )

    private fun resolveIcons(
        teacher: Boolean,
        sequence: Sequence,
        userActiveInteraction: Interaction?
    ): List<AssignmentOverviewModel.PhaseIcon> =
        if (sequence.executionIsFaceToFace() || !teacher) {
            when {
                sequence.isStopped() ->
                    if (sequence.resultsArePublished)
                        listOf(chartIcon)
                    else
                        listOf(lockIcon)

                else -> when (userActiveInteraction?.interactionType) {
                    null -> listOf(minusIcon)
                    InteractionType.ResponseSubmission -> listOf(commentIcon)
                    InteractionType.Evaluation -> listOf(commentsIcon)
                    InteractionType.Read -> listOf(chartIcon)
                }

            }

        } else { // Distance & blended for teacher
            when (sequence.state) {
                State.beforeStart -> listOf(minusIcon)
                State.afterStop ->
                    if (sequence.resultsArePublished) listOf(chartIcon)
                    else listOf(lockIcon)

                else ->
                    if (sequence.resultsArePublished) listOf(commentIcon, commentsIcon, chartIcon)
                    else listOf(commentIcon, commentsIcon)
            }
        }

    private fun resolveRevisionTag(
        sequence: Sequence,
        revisionMode: RevisionMode
    ): Boolean = when (revisionMode) {
        RevisionMode.NotAtAll -> false
        RevisionMode.Immediately -> true
        RevisionMode.AfterTeachings -> sequence.resultsArePublished && (sequence.executionIsFaceToFace() || sequence.isStopped())
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
        audience = if (teacher) {
            assignment.audience +
                    if (assignment.scholarYear != null) {
                        " (${assignment.scholarYear})"
                    } else ""
        } else null,
        assignmentId = assignment.id!!,
        sequences = listOf(
            AssignmentOverviewModel.SequenceInfo(
                id = selectedSequence.id!!,
                title = selectedSequence.statement.title,
                content = selectedSequence.statement.content,
                hideStatementContent = !teacher && selectedSequence.state == State.beforeStart,
                icons = resolveIcons(
                    teacher,
                    selectedSequence,
                    userActiveInteraction
                ),
                revisionTag = resolveRevisionTag(selectedSequence, assignment.revisionMode)
            )
        ),
        selectedSequenceId = selectedSequence.id,
        isRevisionMode = assignment.revisionMode != RevisionMode.NotAtAll,
        indexOfSelectedSequence = assignment.sequences.indexOfFirst { it.id == selectedSequence.id }
    )
}
