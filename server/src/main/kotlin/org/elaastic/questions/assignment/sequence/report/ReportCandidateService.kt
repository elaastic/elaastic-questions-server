/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.moderation

import com.fasterxml.jackson.databind.ObjectMapper
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/** Service for moderation management */
@Service
@Transactional
class ReportCandidateService {

    /** Hide a ReportCandidate. */
    fun markAsHidden(
        reportCandidate: ReportCandidate,
        repository: ReportCandidateRepository,
    ) {
        reportCandidate.hiddenByTeacher = true
        repository.save(reportCandidate)
    }

    /** Remove a ModerationCandidate. */
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

    /** Unhide a ReportCandidate. */
    fun markAsShown(
        reportCandidate: ReportCandidate,
        repository: ReportCandidateRepository,
    ) {
        reportCandidate.hiddenByTeacher = false
        repository.save(reportCandidate)
    }

    fun markAsRestored(
        reportCandidate: ReportCandidate,
        repository: ReportCandidateRepository
    ) {
        reportCandidate.removedByTeacher = false
        repository.save(reportCandidate)
    }
}
