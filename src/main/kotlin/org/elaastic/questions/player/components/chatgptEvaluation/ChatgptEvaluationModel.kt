package org.elaastic.questions.player.components.chatgptEvaluation

import java.math.BigDecimal

data class ChatgptEvaluationModel(
    val annotation: String?,
    val grade: BigDecimal?,
    val status: String?,
    val sequenceId: Long
)
