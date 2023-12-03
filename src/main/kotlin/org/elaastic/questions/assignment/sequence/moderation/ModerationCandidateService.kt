package org.elaastic.questions.assignment.sequence.moderation;

import com.fasterxml.jackson.databind.ObjectMapper
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.springframework.stereotype.Service;

/**
 * Service for moderation management
 */
@Service
public class ModerationCandidateService {

    /**
     * Hide a ModerationCandidate.
     */
    fun markAsHidden(moderationCandidate: ModerationCandidate,
                     repository: ModerationCandidateRepository,
    ) {
        moderationCandidate.hiddenByTeacher = true
        repository.save(moderationCandidate)
    }

    /**
     * Remove a ModerationCandidate.
     */
    fun markAsRemoved(moderationCandidate: ModerationCandidate,
                      repository: ModerationCandidateRepository
    ) {
        moderationCandidate.removedByTeacher = true
        repository.save(moderationCandidate)
    }

    /**
     * Update the report associated with a candidate.
     *
     * @param reportCandidate the candidate to update
     * @param reportReasons the reasons for the report
     * @param reportComment the comment for the report
     * @param repository the repository to save the candidate
     */
    fun updateReport(reportCandidate: ModerationCandidate,
                     reportReasons: List<String>,
                     reportComment : String? = null,
                     repository: ModerationCandidateRepository
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
    fun updateGrade(utilityGradeCandidate: ModerationCandidate,
                    grade: UtilityGrade,
                    repository: ModerationCandidateRepository
    ) {
        utilityGradeCandidate.utilityGrade = grade
        repository.save(utilityGradeCandidate)
    }
}
