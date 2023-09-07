package org.elaastic.questions.player.components.chatGptEvaluation

import org.elaastic.questions.assignment.sequence.ReportReason
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluationStatus
import java.math.BigDecimal

data class ChatGptEvaluationModel(
    val evaluationId: Long? = null,
    val annotation: String? = null,
    val grade: BigDecimal? = null,
    val status: String? = ChatGptEvaluationStatus.UNKNOWN.name,
    val sequenceId: Long,
    val utilityGrade: UtilityGrade? = null,
    val utilityGradeValues: Array<UtilityGrade> = UtilityGrade.values(),
    val reportValues: Array<ReportReason> = ReportReason.values()
)
