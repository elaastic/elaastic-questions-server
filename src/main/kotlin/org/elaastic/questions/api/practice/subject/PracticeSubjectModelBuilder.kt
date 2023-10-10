package org.elaastic.questions.api.practice.subject

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder
import org.elaastic.questions.api.practice.subject.question.PracticeQuestionModelBuilder
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