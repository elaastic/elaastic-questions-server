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

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder
import org.elaastic.consolidation.subject.RestPracticeSubjectController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.linkTo
import java.util.*

/**
 * @author John Tranier
 */
object PracticeQuestionModelBuilder {


    /**
     * Builds the json:api entity representation of PracticeQuestion
     */
    fun build(subjectId: UUID, question: PracticeQuestion) =
        JsonApiModelBuilder.jsonApiModel()
            .model(question)
            .relationshipWithDataArray("explanations")
            .relationship("explanations", question.explanations)
            .let { modelBuilder ->

                question.attachment?.let { attachment ->
                    modelBuilder.relationship(
                        "attachment",
                        EntityModel.of(attachment),
                        null,
                        linkTo<RestPracticeSubjectController> {
                            getAttachmentBlob(
                                subjectId,
                                question.id,
                                question.attachment.id
                            )
                        }.toString()
                    )
                }

                modelBuilder
            }
            .build()
}