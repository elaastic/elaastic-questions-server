/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.ai.evaluation.chatgpt

import org.elaastic.activity.response.Response
import org.elaastic.moderation.ReportCandidateRepository
import org.elaastic.sequence.interaction.Interaction
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