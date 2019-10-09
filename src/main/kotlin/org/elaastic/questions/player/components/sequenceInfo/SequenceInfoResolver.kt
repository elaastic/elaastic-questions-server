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

package org.elaastic.questions.player.components.sequenceInfo

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.controller.MessageBuilder
import java.lang.IllegalStateException

object SequenceInfoResolver {

    fun resolve(isTeacher: Boolean, sequence: Sequence, messageBuilder: MessageBuilder): SequenceInfoModel =
            when (sequence.state) {
                State.beforeStart -> SequenceInfoModel(
                        messageBuilder.message(
                                "player.sequence.beforeStart.message"
                        ),
                        color = "warning",
                        refreshable = !isTeacher
                )
                State.show -> if (sequence.executionContext == ExecutionContext.FaceToFace) {
                    if (sequence.activeInteraction?.interactionType in listOf(InteractionType.ResponseSubmission, InteractionType.Evaluation)) {
                        when (sequence.activeInteraction?.state) {
                            null -> throw IllegalStateException()
                            State.beforeStart -> SequenceInfoModel(
                                    messageBuilder.message(
                                            "player.sequence.interaction.beforeStart.message",
                                            sequence.activeInteraction?.rank?.toString() ?: ""
                                    ),
                                    color = "blue",
                                    refreshable = !isTeacher
                            )
                            State.show -> SequenceInfoModel(
                                    messageBuilder.message(
                                            "player.sequence.interaction.inprogress",
                                            sequence.activeInteraction?.rank?.toString() ?: ""
                                    ),
                                    color = "blue",
                                    refreshable = !isTeacher
                            )
                            State.afterStop -> SequenceInfoModel(
                                    messageBuilder.message(
                                            "player.sequence.interaction.closed.forTeacher",
                                            sequence.activeInteraction?.rank?.toString() ?: ""
                                    ),
                                    color = "blue",
                                    refreshable = !isTeacher
                            )
                        }
                    } else {
                        when {
                            sequence.resultsArePublished -> SequenceInfoModel(
                                    messageBuilder.message(
                                            "player.sequence.interaction.read.teacher.show.message"
                                    ),
                                    color = "blue"
                            )
                            sequence.state == State.show -> SequenceInfoModel(
                                    messageBuilder.message(
                                            "player.sequence.readinteraction.beforeStart.message",
                                            sequence.activeInteraction?.rank?.toString() ?: ""
                                    ),
                                    color = "blue",
                                    refreshable = !isTeacher
                            )
                            else -> SequenceInfoModel(
                                    messageBuilder.message(
                                            "player.sequence.readinteraction.not.published"
                                    ),
                                    refreshable = !isTeacher
                            )
                        }
                    }
                } else {
                    SequenceInfoModel(
                            messageBuilder.message(
                                    "player.sequence.open"
                            ),
                            color = "blue"
                    )
                }
                State.afterStop -> SequenceInfoModel(
                        messageBuilder.message("player.sequence.closed")
                )
            }


}
