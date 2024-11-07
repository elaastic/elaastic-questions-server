package org.elaastic.user.cas

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.CrudRepository

interface CasUserRepository : CrudRepository<CasUser, Long> {

    @EntityGraph(value = "CasUser.user.roles", type = EntityGraph.EntityGraphType.LOAD)
    fun findByCasKeyAndCasUserId(casKey: String, casUserId: String): CasUser?
}