package org.elaastic.player.sequence.status

import org.elaastic.common.web.MessageBuilder
import org.elaastic.sequence.ExecutionContext
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.InteractionType

object SequenceInfoResolver {

    fun resolve(
        isTeacher: Boolean,
        sequence: Sequence,
        messageBuilder: MessageBuilder,
        nbReportedEvaluation: Pair<Int, Int> = 0 to 0,
    ): SequenceInfoModel = when (sequence.state) {
        State.beforeStart -> SequenceInfoModel(
            messageBuilder.message(
                "player.sequence.beforeStart.message"
            ),
            color = "warning",
            refreshable = !isTeacher
        )

        State.afterStop -> SequenceInfoModel(
            messageBuilder.message("player.sequence.closed")
        )

        State.show ->
            if (sequence.executionContext != ExecutionContext.FaceToFace) {
                SequenceInfoModel(
                    messageBuilder.message(
                        "player.sequence.open"
                    ),
                    color = "blue",
                    refreshable = true,
                    nbReportTotal = nbReportedEvaluation.first,
                    nbReportToModerate = nbReportedEvaluation.second,
                )
            } else {
                when (sequence.activeInteractionType) {
                    InteractionType.ResponseSubmission,
                    InteractionType.Evaluation
                        -> {
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
                                refreshable = true
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
                    }

                    // Read interaction
                    else -> getSequenceInfoModelWhenReadInteraction(
                        sequence,
                        messageBuilder,
                        nbReportedEvaluation,
                    )
                }
            }
    }

    /**
     * @param sequence [Sequence] to get the information
     * @param messageBuilder [MessageBuilder] to get the message from the
     *    properties
     * @return [SequenceInfoModel] for read interaction and the execution
     *    context is Blended or Distance
     */
    private fun getSequenceInfoModelWhenReadInteraction(
        sequence: Sequence,
        messageBuilder: MessageBuilder,
        nbReportedEvaluation: Pair<Int, Int>,
    ) = if (sequence.resultsArePublished) {
        SequenceInfoModel(
            messageBuilder.message(
                "player.sequence.interaction.read.teacher.show.message"
            ),
            color = "blue",
            nbReportTotal = nbReportedEvaluation.first,
            nbReportToModerate = nbReportedEvaluation.second,
        )
    } else if (sequence.state == State.show) {
        SequenceInfoModel(
            messageBuilder.message(
                "player.sequence.readinteraction.beforeStart.message",
                sequence.activeInteraction?.rank?.toString() ?: ""
            ),
            color = "blue",
            refreshable = true,
            nbReportTotal = nbReportedEvaluation.first,
            nbReportToModerate = nbReportedEvaluation.second,
        )
    } else {
        SequenceInfoModel(
            messageBuilder.message(
                "player.sequence.readinteraction.not.published"
            ),
            refreshable = true,
            nbReportTotal = nbReportedEvaluation.first,
            nbReportToModerate = nbReportedEvaluation.second,
        )
    }
}