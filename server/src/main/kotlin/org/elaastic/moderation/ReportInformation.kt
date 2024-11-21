package org.elaastic.moderation

/**
 * Information about the number of reports and the number of reports to moderate for an object who manage [ReportCandidate].
 * @param nbReportTotal the total number of reports
 * @param nbReportToModerate the number of reports to moderate
 */
data class ReportInformation(
    val nbReportTotal: Int,
    val nbReportToModerate: Int
) {
    companion object {
        val empty = ReportInformation(0, 0)
    }
}

