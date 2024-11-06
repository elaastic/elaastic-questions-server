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

/**
 * A ReportedCandidateModel is a ReportCandidate that have been reported
 * and been use by a Thymeleaf template.
 *
 * @param id the id of the reported item
 * @param contentReported the content that has been reported
 * @param reportReasons the reason(s) for the report
 * @param reportComment the comment of the reporter
 * @see ReportManagerController
 */
open class ReportedCandidateModel(
    val id: Long,
    val contentReported: String,
    private val reportReasons: String?,
    val reportComment: String?,
    val type: ReportedCandidateType
) {
    private val objectMapper = ObjectMapper()

    val reasons: List<ReportReason> = reportReasons?.let {
        objectMapper.readValue(
            it,
            objectMapper.typeFactory.constructCollectionType(List::class.java, ReportReason::class.java)
        )
    } ?: emptyList()
}

enum class ReportedCandidateType {
    PEER_GRADING,
    CHAT_GPT_EVALUATION,
}