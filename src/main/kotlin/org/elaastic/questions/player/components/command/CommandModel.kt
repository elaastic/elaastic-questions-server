package org.elaastic.questions.player.components.command

import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.directory.User

data class CommandModel(
        val sequenceId: Long,
        val interactionId: Long,
        val interactionRank: Int,

        val actionStartInteraction: ActionStatus,
        val actionStopInteraction: ActionStatus,
        val actionStartNextInteraction: ActionStatus,
        val actionReopenInteraction: ActionStatus,
        val actionReopenSequence: ActionStatus,
        val actionStopSequence: ActionStatus,
        val actionPublishResults: ActionStatus,
        val actionUnpublishResults: ActionStatus

) {
    constructor(user: User, interaction: Interaction) :
            this(
                    sequenceId = interaction.sequence.id!!,
                    interactionId = interaction.id!!,
                    interactionRank = interaction.rank,
                    actionStartInteraction = when {
                        interaction.isRead() || interaction.getStateForTeacher(user) == State.afterStop -> ActionStatus.HIDDEN
                        interaction.getStateForTeacher(user) == State.show -> ActionStatus.DISABLED
                        else -> ActionStatus.ENABLED
                    },
                    actionStopInteraction =
                    if (interaction.getStateForTeacher(user) != State.show || interaction.isRead())
                        ActionStatus.HIDDEN
                    else ActionStatus.ENABLED,
                    actionStartNextInteraction =
                    if (interaction.getStateForTeacher(user) != State.afterStop || !interaction.isResponseSubmission())
                        ActionStatus.HIDDEN
                    else ActionStatus.ENABLED,
                    actionReopenInteraction =
                    if (interaction.getStateForTeacher(user) != State.afterStop || !interaction.isResponseSubmission())
                        ActionStatus.HIDDEN
                    else ActionStatus.ENABLED,
                    actionReopenSequence = when {
                        !interaction.sequence.isStopped() -> ActionStatus.HIDDEN
                        interaction.sequence.executionIsFaceToFace() && interaction.isRead() -> ActionStatus.HIDDEN
                        else -> ActionStatus.ENABLED
                    },
                    actionStopSequence = if (interaction.sequence.state != State.show)
                        ActionStatus.HIDDEN
                    else ActionStatus.ENABLED,
                    actionPublishResults = if (!interaction.sequence.resultsCanBePublished())
                        ActionStatus.HIDDEN
                    else ActionStatus.ENABLED,
                    actionUnpublishResults = if (!interaction.sequence.resultsArePublished)
                        ActionStatus.HIDDEN
                    else ActionStatus.ENABLED
            )

    enum class ActionStatus {
        ENABLED,
        DISABLED,
        HIDDEN
    }
}