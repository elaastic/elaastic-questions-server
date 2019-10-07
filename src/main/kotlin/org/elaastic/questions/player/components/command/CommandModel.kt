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

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.directory.User

data class CommandModel(
        val sequenceId: Long,
        val interactionId: Long?,
        val interactionRank: Int?,
        val questionType: QuestionType,

        val actionStartSequence: ActionStatus,
        val actionStartInteraction: ActionStatus,
        val actionStopInteraction: ActionStatus,
        val actionStartNextInteraction: ActionStatus,
        val actionReopenInteraction: ActionStatus,
        val actionReopenSequence: ActionStatus,
        val actionStopSequence: ActionStatus,
        val actionPublishResults: ActionStatus,
        val actionUnpublishResults: ActionStatus

) {

    enum class ActionStatus {
        ENABLED,
        DISABLED,
        HIDDEN
    }
}
