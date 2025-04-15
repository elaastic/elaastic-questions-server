package org.elaastic.auth

import org.elaastic.user.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.CrudRepository

interface UserLinkRepository : CrudRepository<UserLink, Long> {

    @EntityGraph(value = "UserLink.user.roles", type = EntityGraph.EntityGraphType.LOAD)
    fun findByProviderIdAndProviderUserId(providerId: String, providerUserId: String): UserLink?

    fun findByUser(user: User): UserLink?
}