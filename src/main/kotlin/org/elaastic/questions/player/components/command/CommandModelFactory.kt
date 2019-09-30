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
                            interaction == null -> CommandModel.ActionStatus.HIDDEN
                            interaction.isRead() || interaction.getStateForTeacher(user) == State.afterStop -> CommandModel.ActionStatus.HIDDEN
                            interaction.getStateForTeacher(user) == State.show -> CommandModel.ActionStatus.DISABLED
                            else -> CommandModel.ActionStatus.ENABLED
                        },
                        actionStopInteraction =
                        if (interaction == null || interaction.getStateForTeacher(user) != State.show || interaction.isRead())
                            CommandModel.ActionStatus.HIDDEN
                        else CommandModel.ActionStatus.ENABLED,
                        actionStartNextInteraction =
                        if (interaction == null || interaction.getStateForTeacher(user) != State.afterStop || !interaction.isResponseSubmission())
                            CommandModel.ActionStatus.HIDDEN
                        else CommandModel.ActionStatus.ENABLED,
                        actionReopenInteraction =
                        if (interaction == null || interaction.getStateForTeacher(user) != State.afterStop || !interaction.isResponseSubmission())
                            CommandModel.ActionStatus.HIDDEN
                        else CommandModel.ActionStatus.ENABLED,
                        actionReopenSequence = when {
                            !sequence.isStopped() -> CommandModel.ActionStatus.HIDDEN
                            sequence.executionIsFaceToFace() && interaction?.isRead() ?: false -> CommandModel.ActionStatus.HIDDEN
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