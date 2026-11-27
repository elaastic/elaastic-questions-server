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

package org.elaastic.consolidation.subject

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder
import org.elaastic.consolidation.subject.question.PracticeQuestionModelBuilder
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.linkTo

/**
 * Builds json:api representations of a practice subject
 *
 * @author John Tranier
 */
object PracticeSubjectModelBuilder {

    /**
     * Provides a summary representation of a practice subject
     * Composed of
     * - its id
     * - its title
     * - the entity type
     * - its self URL (allowing for retrieving the detailed practice subject data)
     */
    fun buildSummary(practiceSubject: SummaryPracticeSubject) = EntityModel.of(
        practiceSubject,
        linkTo<RestPracticeSubjectController> { getPracticeSubject(practiceSubject.id) }.withSelfRel()
    )

    /**
     * Provided detailed representation of a practice subject
     * Composed of
     * - its title
     * - its topic
     * - its questions
     */
    fun buildDetailed(subject: PracticeSubject) = JsonApiModelBuilder.jsonApiModel()
        .model(subject)
        .link(linkTo<RestPracticeSubjectController> { getPracticeSubject(subject.id) }.withSelfRel())
        .let { modelBuilder ->
            // Topic
            if (subject.topic != null) {
                modelBuilder.relationship("topic", subject.topic)
                modelBuilder.included(EntityModel.of(subject.topic))
            }

            // Questions
            if (subject.questions.isNotEmpty()) {
                modelBuilder
                    .relationshipWithDataArray("questions")
                    .relationship("questions", subject.questions)

                subject.questions.forEach { question ->
                    modelBuilder.included(PracticeQuestionModelBuilder.build(subject.id, question))
                        .included(question.explanations.map { EntityModel.of(it) })

                    if (question.attachment !== null) {
                        modelBuilder.included(
                            EntityModel.of(
                                question.attachment,
                                linkTo<RestPracticeSubjectController> {
                                    getAttachmentBlob(
                                        subject.id,
                                        question.id,
                                        question.attachment.id
                                    )
                                }.withRel("blob")
                            )
                        )
                    }
                }
            }

            // Learners
            modelBuilder
                .relationshipWithDataArray("learners")
                .relationship("learners", subject.learners)
                .included(subject.learners.map { EntityModel.of(it) })

            modelBuilder

        }
        .build()
}