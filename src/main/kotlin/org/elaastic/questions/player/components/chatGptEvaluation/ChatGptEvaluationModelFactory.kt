package org.elaastic.questions.player.components.chatGptEvaluation

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluation

object ChatGptEvaluationModelFactory {

    fun build(
        evaluation: ChatGptEvaluation?,
        sequence: Sequence
    ) : ChatGptEvaluationModel = ChatGptEvaluationModel(
        evaluation?.annotation,
        evaluation?.grade?.stripTrailingZeros(),
        evaluation?.status,
        sequence.id!!
    )
}