package org.elaastic.questions.player.components.sequenceInfo

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.controller.MessageBuilder
import java.lang.IllegalStateException

object SequenceInfoResolver {

    fun resolve(sequence: Sequence, messageBuilder: MessageBuilder): SequenceInfoModel =
            when (sequence.state) {
                State.beforeStart -> SequenceInfoModel(
                        messageBuilder.message(
                                "player.sequence.beforeStart.message"
                        ),
                        color = "warning"
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
                                    color = "blue"
                            )
                            State.show -> SequenceInfoModel(
                                    messageBuilder.message(
                                            "player.sequence.interaction.inprogress",
                                            sequence.activeInteraction?.rank?.toString() ?: ""
                                    ),
                                    color = "blue"
                            )
                            State.afterStop -> SequenceInfoModel(
                                    messageBuilder.message(
                                            "player.sequence.interaction.closed.forTeacher",
                                            sequence.activeInteraction?.rank?.toString() ?: ""
                                    ),
                                    color = "blue"
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
                                    color = "blue"
                            )
                            else -> SequenceInfoModel(
                                    messageBuilder.message(
                                            "player.sequence.readinteraction.not.published"
                                    )
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