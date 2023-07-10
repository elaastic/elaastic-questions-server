package org.elaastic.questions.api.practice.subject.question

import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.questions.assignment.sequence.interaction.response.Response

/**
 * Represent an explanation of an answer to a question provided by a learner.
 * It will be presented as a feedback to learners that practice on the bound question.
 * @author John Tranier
 */
class PracticeLearnerExplanation(
    @JsonApiId
    val id: Long,
    val explanation: String
) {

    @JsonApiType
    val type = "practice-learner-explanation"

    constructor(response: Response): this(
        id = response.id!!,
        explanation = response.explanation ?: ""
    )
}