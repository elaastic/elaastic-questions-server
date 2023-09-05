package org.elaastic.questions.player.components.chatgptEvaluation

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.chatgptEvaluation.ChatgptEvaluation

object ChatgptEvaluationModelFactory {

    fun build(
        evaluation: ChatgptEvaluation?,
        sequence: Sequence
    ) : ChatgptEvaluationModel = ChatgptEvaluationModel(
        evaluation?.annotation,
        evaluation?.grade?.stripTrailingZeros(),
        evaluation?.status,
        sequence.id!!
    )
}