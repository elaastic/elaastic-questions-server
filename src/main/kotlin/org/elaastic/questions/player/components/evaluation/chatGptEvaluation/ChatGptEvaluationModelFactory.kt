package org.elaastic.questions.player.components.evaluation.chatGptEvaluation

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluation

object ChatGptEvaluationModelFactory {

    fun build(
        evaluation: ChatGptEvaluation?,
        sequence: Sequence,
        canHideGrading: Boolean = false,
        responseId: Long? = null
    ) : ChatGptEvaluationModel = ChatGptEvaluationModel(
        evaluationId = evaluation?.id,
        annotation = evaluation?.annotation,
        grade = evaluation?.grade?.stripTrailingZeros(),
        status = evaluation?.status,
        sequenceId = sequence.id!!,
        utilityGrade = evaluation?.utilityGrade,
        gradingID = evaluation?.id,
        hiddenByTeacher = evaluation?.hiddenByTeacher ?: false,
        canHideGrading = canHideGrading,
        responseId = responseId,
    )
}