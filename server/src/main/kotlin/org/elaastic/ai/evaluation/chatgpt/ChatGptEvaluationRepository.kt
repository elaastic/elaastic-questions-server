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

    @Query(
        "SELECT gpt " +
                "FROM ChatGptEvaluation gpt " +
                "JOIN Response r on gpt.response = r " +
                "JOIN Interaction i on r.interaction = i " +
                "WHERE gpt.removedByTeacher = :removed " +
                "AND gpt.reportReasons IS NOT NULL " +
                "AND i = :interaction"
    )
    fun findAllReported(interaction: Interaction, removed: Boolean = false): List<ChatGptEvaluation>

    @Query(
        "SELECT COUNT(gpt) " +
                "FROM ChatGptEvaluation gpt " +
                "JOIN Response r on gpt.response = r " +
                "JOIN Interaction i on r.interaction = i " +
                "WHERE gpt.removedByTeacher = :removed " +
                "AND gpt.reportReasons IS NOT NULL " +
                "AND i = :interaction"
    )
    fun countAllReported(interaction: Interaction, removed: Boolean = false): Int
}