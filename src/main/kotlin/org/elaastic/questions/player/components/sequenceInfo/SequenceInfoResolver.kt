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
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluationService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.player.phase.evaluation.EvaluationPhaseConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
object SequenceInfoResolver {

    @Autowired lateinit var peerGradingService: PeerGradingService
    @Autowired lateinit var chatGptEvaluationService: ChatGptEvaluationService

    fun resolve(isTeacher: Boolean, sequence: Sequence, messageBuilder: MessageBuilder): SequenceInfoModel {
        val nbReportedEvaluation = getReportedEvaluation(sequence, isTeacher)

        return when (sequence.state) {
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
                    )
                } else {
                    when (sequence.activeInteraction?.interactionType) {
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
                        else -> getSequenceInfoModelWhenReadInteraction(sequence, messageBuilder, nbReportedEvaluation)
                    }
                }
        }
    }

    /**
     * @param sequence [Sequence] to get the information
     * @param teacher [Boolean] to know if the user is a teacher
     * @return [Int] for the reported evaluation
     */
    private fun getReportedEvaluation(sequence: Sequence, teacher: Boolean): Int {
        if (!teacher) {
            return 0
        }

        val nbDRAXOEvaluationReported: Int = if (sequence.evaluationPhaseConfig == EvaluationPhaseConfig.DRAXO) {
            peerGradingService.findAllDraxoPeerGradingReportedNotHidden(sequence).count()
        } else {
            0
        }

        val nbChatGPTEvaluationReported: Int = if (sequence.chatGptEvaluationEnabled) {
            chatGptEvaluationService.findAllReportedNotHidden(sequence).count()
        } else {
            0
        }

        return nbDRAXOEvaluationReported + nbChatGPTEvaluationReported
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
        nbReportedEvaluation: Int = 0,
    ) = if (sequence.resultsArePublished) {
        SequenceInfoModel(
            messageBuilder.message(
                "player.sequence.interaction.read.teacher.show.message"
            ),
            color = "blue",
            nbEvaluationReported = nbReportedEvaluation,
        )
    } else if (sequence.state == State.show) {
        SequenceInfoModel(
            messageBuilder.message(
                "player.sequence.readinteraction.beforeStart.message",
                sequence.activeInteraction?.rank?.toString() ?: ""
            ),
            color = "blue",
            refreshable = true,
            nbEvaluationReported = nbReportedEvaluation,
        )
    } else {
        SequenceInfoModel(
            messageBuilder.message(
                "player.sequence.readinteraction.not.published"
            ),
            refreshable = true,
            nbEvaluationReported = nbReportedEvaluation,
        )
    }
}
