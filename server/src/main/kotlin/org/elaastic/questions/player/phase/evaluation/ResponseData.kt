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

package org.elaastic.questions.player.phase.evaluation

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.sequence.interaction.response.Response

interface ResponseData {
    val id: Long
    val explanation: String
    val questionType: QuestionType
    fun getChoiceList() = listOf<Int>()
}

data class OpenEndedResponseData(
    override val id: Long,
    override val explanation: String
) : ResponseData {
    override val questionType = QuestionType.OpenEnded

    constructor(response: Response): this(
        id = response.id ?: error("Response has no ID"),
        explanation = response.explanation ?: "",
    )
}

data class ExclusiveChoiceResponseData(
    override val id: Long,
    override val explanation: String,
    val choice: Int?
) : ResponseData {
    override val questionType = QuestionType.ExclusiveChoice
    override fun getChoiceList() = choice?.let { listOf(choice) } ?: listOf()

    constructor(response: Response): this(
        id = response.id ?: error("Response has no ID"),
        explanation = response.explanation ?: "",
        choice = response.learnerChoice?.firstOrNull()
    )
}

data class MultipleChoiceResponseData(
    override val id: Long,
    override val explanation: String,
    val choices: List<Int>
) : ResponseData {
    override val questionType = QuestionType.MultipleChoice
    override fun getChoiceList() = choices

    constructor(response: Response): this(
        id = response.id ?: error("Response has no ID"),
        explanation = response.explanation ?: "",
        choices = response.learnerChoice ?: listOf()
    )
}

object ResponseDataFactory {
    fun build(response: Response) = when (response.statement.questionType) {
        QuestionType.OpenEnded -> OpenEndedResponseData(response)
        QuestionType.ExclusiveChoice -> ExclusiveChoiceResponseData(response)
        QuestionType.MultipleChoice -> MultipleChoiceResponseData(response)
    }
}