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

    const val revisionIcon = "big grey graduation cap"
    const val chartIcon = "big grey bar chart outline"
    const val lockIcon = "big grey lock"
    const val minusIcon = "big grey minus"
    const val commentIcon = "big grey comment outline"
    const val commentsIcon = "big grey comments outline"

    fun build(
        teacher: Boolean,
        assignment: Assignment,
        nbRegisteredUser: Int,
        sequenceToUserActiveInteraction: Map<Sequence, Interaction?>,
        selectedSequenceId: Long? = null
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
                    sequenceToUserActiveInteraction[it],
                    assignment.revisionMode
                )
            )
        },
        selectedSequenceId = selectedSequenceId
    )

    private fun resolveIcons(
        teacher: Boolean,
        sequence: Sequence,
        userActiveInteraction: Interaction?,
        revisionMode: RevisionMode
    ): List<PhaseIcon> =
            if (sequence.executionIsFaceToFace() || !teacher) {
            when {
                sequence.isStopped() ->
                    if (sequence.resultsArePublished)
                        if (revisionMode != RevisionMode.NotAtAll)
                            listOf(chartIcon, revisionIcon)
                        else
                            listOf(chartIcon)
                    else
                        if (revisionMode == RevisionMode.Immediately)
                            listOf(lockIcon, revisionIcon)
                        else
                            listOf(lockIcon)

                else -> when (userActiveInteraction?.interactionType) {
                    null -> if (revisionMode == RevisionMode.Immediately) listOf(minusIcon, revisionIcon)
                            else listOf(minusIcon)
                    InteractionType.ResponseSubmission ->
                            if (revisionMode == RevisionMode.Immediately) listOf(commentIcon, revisionIcon)
                            else listOf(commentIcon)
                    InteractionType.Evaluation ->
                            if (revisionMode == RevisionMode.Immediately) listOf(commentsIcon, revisionIcon)
                            else listOf(commentsIcon)
                    InteractionType.Read ->
                            if (revisionMode == RevisionMode.Immediately) listOf(chartIcon, revisionIcon)
                            else listOf(chartIcon)
                }

            }

        } else { // Distance & blended for teacher
            when (sequence.state) {
                State.beforeStart ->
                    if (revisionMode == RevisionMode.Immediately) listOf(minusIcon, revisionIcon)
                    else listOf(minusIcon)
                State.afterStop ->
                    if (sequence.resultsArePublished)
                        if (revisionMode != RevisionMode.NotAtAll) listOf(chartIcon, revisionIcon)
                        else listOf(chartIcon)

                    else
                        if (revisionMode == RevisionMode.Immediately) listOf(lockIcon, revisionIcon)
                        else listOf(lockIcon)

                else ->
                    if (sequence.resultsArePublished)
                        if (revisionMode != RevisionMode.NotAtAll)
                            listOf(commentIcon, commentsIcon, chartIcon, revisionIcon)
                        else
                            listOf(commentIcon, commentsIcon, chartIcon)
                    else
                        if (revisionMode == RevisionMode.Immediately)
                            listOf(commentIcon, commentsIcon, revisionIcon)
                        else
                            listOf(commentIcon, commentsIcon )
            }
        }
}
