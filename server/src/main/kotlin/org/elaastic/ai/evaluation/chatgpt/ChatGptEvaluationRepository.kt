package org.elaastic.ai.evaluation.chatgpt

import org.elaastic.moderation.ReportCandidateRepository
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface ChatGptEvaluationRepository : JpaRepository<ChatGptEvaluation, Long>,
    ReportCandidateRepository {

    fun findByResponse(response: Response): ChatGptEvaluation?

    fun findAllByResponseIn(response: List<Response>): List<ChatGptEvaluation>

    @Query("SELECT gpt " +
            "FROM ChatGptEvaluation gpt " +
            "JOIN Response r on gpt.response = r " +
            "JOIN Interaction i on r.interaction = i " +
            "WHERE gpt.hiddenByTeacher = false " +
            "AND gpt.reportReasons IS NOT NULL " +
            "AND i = :interaction")
    fun findAllReportedNotHidden(interaction: Interaction): List<ChatGptEvaluation>

    @Query("SELECT COUNT(gpt) " +
            "FROM ChatGptEvaluation gpt " +
            "JOIN Response r on gpt.response = r " +
            "JOIN Interaction i on r.interaction = i " +
            "WHERE gpt.hiddenByTeacher = false " +
            "AND gpt.reportReasons IS NOT NULL " +
            "AND i = :interaction")
    fun countAllReportedNotHidden(interaction: Interaction): Int


    fun countAllByHiddenByTeacherIsFalseAndReportReasonsIsNotNullAndResponseIn(responses: List<Response>): Int

}