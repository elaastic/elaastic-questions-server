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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.consolidation.subject.question.attachment.PracticeAttachment
import org.elaastic.consolidation.subject.question.specification.QuestionSpecification
import java.util.UUID

/**
 * Represents a question of a practice subject
 * It may have an attachement.
 * It has a list learner explanations (the 3 best ranked) that will be used as feedbacks provided
 * to learner practicing on this question.
 * 
 * @author John Tranier
 */
class PracticeQuestion(
    @JsonApiId
    val id: UUID,
    val rank: Int,
    val title: String,
    val content: String,
    val expectedExplanation: String,
    val specification: QuestionSpecification,

    @JsonIgnore
    val explanations: List<PracticeLearnerExplanation>,

    @JsonIgnore
    val attachment: PracticeAttachment?,
) {
    @JsonApiType
    val type = "practice-question"
}