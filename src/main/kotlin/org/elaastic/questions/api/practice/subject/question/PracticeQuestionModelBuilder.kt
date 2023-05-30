package org.elaastic.questions.api.practice.subject.question

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder
import org.elaastic.questions.api.practice.subject.RestPracticeSubjectController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.linkTo

/**
 * @author John Tranier
 */
object PracticeQuestionModelBuilder {


    /**
     * Builds the json:api entity representation of PracticeQuestion
     */
    fun build(subjectId: Long, question: PracticeQuestion) =
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