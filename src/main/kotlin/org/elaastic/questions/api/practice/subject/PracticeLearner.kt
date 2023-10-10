package org.elaastic.questions.api.practice.subject

import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.questions.directory.ExternalUserRef
import org.elaastic.questions.directory.User
import java.util.*

/**
 * Represents a learner
 *
 * Each PracticeSubject is associated to the set of PracticeLearners
 * that are allowed to practice it
 *
 * @author John Tranier
 */
class PracticeLearner(
    @JsonApiId
    val id: UUID,
) {

    @JsonApiType
    val type = "practice-learner"

    val externalUserRef = listOf(
        ExternalUserRef("ENT_1", "123456"),
        ExternalUserRef("ENT_2", "789456"),
    )

    constructor(user: User): this(
        id = user.uuid
    )
}