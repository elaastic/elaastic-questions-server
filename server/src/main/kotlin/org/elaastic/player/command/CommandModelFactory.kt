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

package org.elaastic.player.command

import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State

object CommandModelFactory {

    fun build(sequence: Sequence): CommandModel =
        sequence.activeInteraction.let { interaction ->
            val interactionStateForTeacher = interaction?.getStateForTeacher()

            val nextInteraction = runCatching { interaction?.let { sequence.getInteractionAt(it.rank + 1) } }
                .getOrNull()
            val nextNextInteraction = runCatching { nextInteraction?.let { sequence.getInteractionAt(it.rank + 1) } }
                .getOrNull()

            CommandModel(
                sequenceId = sequence.id ?: throw IllegalStateException("This sequence has no ID"),
                statementId = sequence.statement.id ?: throw IllegalStateException("This statement has no ID"),
                interactionId = interaction?.id,
                interactionRank = interaction?.rank,
                nextInteractionRank = nextInteraction?.rank,
                nextNextInteractionRank = nextNextInteraction?.rank,
                questionType = sequence.statement.questionType,
                hasExpectedExplanation = !sequence.statement.expectedExplanation.isNullOrBlank(),

                actionStartSequence =
                    if (sequence.state == State.beforeStart)
                        CommandModel.ActionStatus.ENABLED
                    else CommandModel.ActionStatus.HIDDEN,

                actionStartInteraction =
                    if (interaction == null || sequence.isStopped() || interaction.isRead() || interactionStateForTeacher == State.afterStop)
                        CommandModel.ActionStatus.HIDDEN
                    else if (interactionStateForTeacher == State.show)
                        CommandModel.ActionStatus.DISABLED
                    else CommandModel.ActionStatus.ENABLED,

                actionStopInteraction =
                    if (interaction != null && !sequence.isStopped() && interactionStateForTeacher == State.show && !interaction.isRead())
                        CommandModel.ActionStatus.ENABLED
                    else CommandModel.ActionStatus.HIDDEN,

                actionStartNextInteraction =
                    if (interaction != null && !sequence.isStopped() && interactionStateForTeacher == State.afterStop && interaction.isResponseSubmission())
                        CommandModel.ActionStatus.ENABLED
                    else CommandModel.ActionStatus.HIDDEN,

                actionSkipNextInteraction =
                    if (interaction != null && !sequence.isStopped() && interactionStateForTeacher == State.afterStop && nextInteraction?.isEvaluation() == true && nextNextInteraction?.isRead() == true)
                        CommandModel.ActionStatus.ENABLED
                    else CommandModel.ActionStatus.HIDDEN,

                actionReopenInteraction =
                    if (interaction != null && !sequence.isStopped() && interactionStateForTeacher == State.afterStop && !interaction.isRead())
                        CommandModel.ActionStatus.ENABLED
                    else CommandModel.ActionStatus.HIDDEN,

                actionReopenSequence =
                    if (sequence.isStopped() && (!sequence.executionIsFaceToFace() || interaction?.isRead() == false))
                        CommandModel.ActionStatus.ENABLED
                    else CommandModel.ActionStatus.HIDDEN,

                actionStopSequence =
                    if (sequence.state == State.show)
                        CommandModel.ActionStatus.ENABLED
                    else CommandModel.ActionStatus.HIDDEN,

                actionPublishResults =
                    if (sequence.resultsCanBePublished())
                        CommandModel.ActionStatus.ENABLED
                    else CommandModel.ActionStatus.HIDDEN,

                actionUnpublishResults =
                    if (sequence.resultsArePublished)
                        CommandModel.ActionStatus.ENABLED
                    else CommandModel.ActionStatus.HIDDEN
            )
        }
}
