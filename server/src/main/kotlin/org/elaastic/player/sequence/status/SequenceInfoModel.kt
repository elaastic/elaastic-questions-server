package org.elaastic.player.sequence.status

data class SequenceInfoModel(
    val message: String,
    val color: String? = null,
    val refreshable: Boolean = false,
    val nbReportTotal: Int = 0,
    val nbReportToModerate: Int = 0,
)