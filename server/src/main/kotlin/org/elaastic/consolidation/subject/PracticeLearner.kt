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
        externalUserRef = user.userLink?.let { ExternalUserRef(it.providerId, it.providerUserId) }
    )
}