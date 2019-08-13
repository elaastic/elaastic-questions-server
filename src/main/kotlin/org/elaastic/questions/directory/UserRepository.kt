package org.elaastic.questions.directory

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.CrudRepository


interface UserRepository : CrudRepository<User, Long> {

    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    fun findByUsername(username: String): User?

    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    fun getByUsername(username: String): User
}