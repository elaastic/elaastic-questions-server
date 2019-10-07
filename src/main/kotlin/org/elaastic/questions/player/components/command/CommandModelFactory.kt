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

package org.elaastic.questions.player.components.command

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.directory.User
import java.lang.IllegalStateException

object CommandModelFactory {

    fun build(user: User, sequence: Sequence) : CommandModel =
            sequence.activeInteraction.let { interaction ->
                CommandModel(
                        sequenceId = sequence.id ?: throw IllegalStateException("This sequence hos no ID"),
                        interactionId = interaction?.id,
                        interactionRank = interaction?.rank,
                        questionType = sequence.statement.questionType,
                        actionStartSequence =
                        if(sequence.state == State.beforeStart)
                            CommandModel.ActionStatus.ENABLED
                        else CommandModel.ActionStatus.HIDDEN,
                        actionStartInteraction = when {
                            interaction == null || sequence.isStopped() -> CommandModel.ActionStatus.HIDDEN
                            interaction.isRead() || interaction.getStateForTeacher(user) == State.afterStop -> CommandModel.ActionStatus.HIDDEN
                            interaction.getStateForTeacher(user) == State.show -> CommandModel.ActionStatus.DISABLED
                            else -> CommandModel.ActionStatus.ENABLED
                        },
                        actionStopInteraction =
                        if (interaction == null || sequence.isStopped() || interaction.getStateForTeacher(user) != State.show || interaction.isRead())
                            CommandModel.ActionStatus.HIDDEN
                        else CommandModel.ActionStatus.ENABLED,
                        actionStartNextInteraction =
                        if (interaction == null || sequence.isStopped() || interaction.getStateForTeacher(user) != State.afterStop || !interaction.isResponseSubmission())
                            CommandModel.ActionStatus.HIDDEN
                        else CommandModel.ActionStatus.ENABLED,
                        actionReopenInteraction =
                        if (interaction == null|| sequence.isStopped()  ||  interaction.getStateForTeacher(user) != State.afterStop || !interaction.isResponseSubmission())
                            CommandModel.ActionStatus.HIDDEN
                        else CommandModel.ActionStatus.ENABLED,
                        actionReopenSequence = when {
                            !sequence.isStopped() -> CommandModel.ActionStatus.HIDDEN
                            sequence.executionIsFaceToFace() && (interaction?.isRead() == true) -> CommandModel.ActionStatus.HIDDEN
                            else -> CommandModel.ActionStatus.ENABLED
                        },
                        actionStopSequence = if (sequence.state != State.show)
                            CommandModel.ActionStatus.HIDDEN
                        else CommandModel.ActionStatus.ENABLED,
                        actionPublishResults = if (!sequence.resultsCanBePublished())
                            CommandModel.ActionStatus.HIDDEN
                        else CommandModel.ActionStatus.ENABLED,
                        actionUnpublishResults = if (!sequence.resultsArePublished)
                            CommandModel.ActionStatus.HIDDEN
                        else CommandModel.ActionStatus.ENABLED
                )
            }
}
