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