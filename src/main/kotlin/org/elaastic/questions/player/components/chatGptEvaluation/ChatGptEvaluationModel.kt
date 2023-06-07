package org.elaastic.questions.player.components.chatGptEvaluation

import java.math.BigDecimal

data class ChatGptEvaluationModel(
    val annotation: String?,
    val grade: BigDecimal?,
    val status: String?,
    val sequenceId: Long
)
