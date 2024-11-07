package org.elaastic.player.sequence.status

data class SequenceInfoModel(
        val message: String,
        val color: String? = null,
        val refreshable: Boolean = false,
        val nbEvaluationReported: Int = 0,
)