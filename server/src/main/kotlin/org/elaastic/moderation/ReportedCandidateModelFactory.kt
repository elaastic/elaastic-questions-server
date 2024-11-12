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

import org.elaastic.activity.evaluation.peergrading.draxo.DraxoPeerGrading
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluation

object ReportedCandidateModelFactory {

    fun build(
        reportCandidate: ReportCandidate
    ): ReportedCandidateModel? {
        return when (reportCandidate) {
            is DraxoPeerGrading -> build(reportCandidate)
            is ChatGptEvaluation -> build(reportCandidate)
            else -> null
        }
    }

    fun build(
        draxoPeerGrading: DraxoPeerGrading,
    ): ReportedCandidateModel {
        return ReportedCandidateModel(
            id = draxoPeerGrading.id!!,
            contentReported = draxoPeerGrading.annotation!!,
            reportReasons = draxoPeerGrading.reportReasons,
            reportComment = draxoPeerGrading.reportComment,
            type = ReportedCandidateType.PEER_GRADING,
        )
    }

    fun build(
        chatGptEvaluation: ChatGptEvaluation,
    ): ReportedCandidateModel {
        return ReportedCandidateModel(
            id = chatGptEvaluation.id!!,
            contentReported = chatGptEvaluation.annotation!!,
            reportReasons = chatGptEvaluation.reportReasons,
            reportComment = chatGptEvaluation.reportComment,
            type = ReportedCandidateType.CHAT_GPT_EVALUATION,
        )
    }
}