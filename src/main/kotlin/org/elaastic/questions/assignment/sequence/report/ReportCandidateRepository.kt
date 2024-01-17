package org.elaastic.questions.assignment.sequence.report

/**
 * Marker interface for moderation candidate repository
 */
fun interface ReportCandidateRepository {

    /**
     * Save the candidate.
     *
     * @param reportCandidate the candidate to save.
     */
    fun save(reportCandidate: ReportCandidate)

}