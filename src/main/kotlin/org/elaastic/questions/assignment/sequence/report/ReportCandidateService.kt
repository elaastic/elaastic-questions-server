package org.elaastic.questions.assignment.sequence.report;

import com.fasterxml.jackson.databind.ObjectMapper
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.springframework.stereotype.Service;

/**
 * Service for moderation management
 */
@Service
public class ReportCandidateService {

    /**
     * Hide a ReportCandidate.
     */
    fun markAsHidden(
        reportCandidate: ReportCandidate,
        repository: ReportCandidateRepository,
    ) {
        reportCandidate.hiddenByTeacher = true
        repository.save(reportCandidate)
        if (reportCandidate is PeerGrading) reportCandidate.response.draxoEvaluationHiddenCount++
    }

    /**
     * Remove a ModerationCandidate.
     */
    fun markAsRemoved(
        reportCandidate: ReportCandidate,
        repository: ReportCandidateRepository
    ) {
        reportCandidate.removedByTeacher = true
        repository.save(reportCandidate)
    }

    /**
     * Update the report associated with a candidate.
     *
     * @param reportCandidate the candidate to update
     * @param reportReasons the reasons for the report
     * @param reportComment the comment for the report
     * @param repository the repository to save the candidate
     */
    fun updateReport(
        reportCandidate: ReportCandidate,
        reportReasons: List<String>,
        reportComment: String? = null,
        repository: ReportCandidateRepository
    ) {
        val objectMapper = ObjectMapper()
        val jsonString = objectMapper.writeValueAsString(reportReasons)
        reportCandidate.reportReasons = jsonString
        reportCandidate.reportComment = reportComment
        repository.save(reportCandidate)
    }

    /**
     * Update the grade associated with a candidate.
     *
     * @param utilityGradeCandidate the candidate to update
     * @param grade the grade
     * @param repository the repository to save the candidate
     */
    fun updateGrade(
        utilityGradeCandidate: ReportCandidate,
        grade: UtilityGrade,
        repository: ReportCandidateRepository
    ) {
        utilityGradeCandidate.utilityGrade = grade
        repository.save(utilityGradeCandidate)
    }

    /**
     * Unhide a ReportCandidate.
     */
    fun markAsShown(
        reportCandidate: ReportCandidate,
        repository: ReportCandidateRepository,
    ) {
        reportCandidate.hiddenByTeacher = false
        repository.save(reportCandidate)
        if (reportCandidate is PeerGrading) reportCandidate.response.draxoEvaluationHiddenCount--
    }
}
