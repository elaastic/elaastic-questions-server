package org.elaastic.ai.evaluation.chatgpt

import org.elaastic.questions.assignment.sequence.report.ReportCandidateRepository
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.springframework.data.jpa.repository.JpaRepository


interface ChatGptEvaluationRepository : JpaRepository<ChatGptEvaluation, Long>,
    ReportCandidateRepository {

    fun findByResponse(response: Response): ChatGptEvaluation?

    fun findAllByResponseIn(response: List<Response>): List<ChatGptEvaluation>

    fun countAllByHiddenByTeacherIsFalseAndReportReasonsIsNotNullAndResponseIn(responses: List<Response>): Int

}