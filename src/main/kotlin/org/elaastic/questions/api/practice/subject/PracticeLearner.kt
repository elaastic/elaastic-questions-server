package org.elaastic.questions.api.practice.subject

import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.questions.directory.User

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
    val id: Long,
) {

    @JsonApiType
    val type = "practice-learner"

    constructor(user: User): this(
        id = user.id!!
    )
}