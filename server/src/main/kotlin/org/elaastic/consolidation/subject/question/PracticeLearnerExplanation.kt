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

package org.elaastic.consolidation.subject.question

import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.activity.response.Response
import java.util.UUID

/**
 * Represent an explanation of an answer to a question provided by a learner.
 * It will be presented as a feedback to learners that practice on the bound question.
 * @author John Tranier
 */
class PracticeLearnerExplanation(
    @JsonApiId
    val id: UUID,
    val explanation: String
) {

    @JsonApiType
    val type = "practice-learner-explanation"

    constructor(response: Response): this(
        id = response.uuid,
        explanation = response.explanation ?: ""
    )
}