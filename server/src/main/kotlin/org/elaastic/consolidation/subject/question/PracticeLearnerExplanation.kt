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