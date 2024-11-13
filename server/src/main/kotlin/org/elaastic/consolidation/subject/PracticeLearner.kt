package org.elaastic.consolidation.subject

import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.user.ExternalUserRef
import org.elaastic.user.User
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
    val externalUserRef: ExternalUserRef?
) {

    @JsonApiType
    val type = "practice-learner"

    constructor(user: User) : this(
        id = user.uuid,
        externalUserRef = user.casUser?.let { ExternalUserRef(it.casKey, it.casUserId) }
    )
}