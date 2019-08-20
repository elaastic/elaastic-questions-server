package org.elaastic.questions.directory

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository


interface UserRepository : CrudRepository<User, Long>, JpaRepository<User, Long> {

    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    fun findByUsername(username: String): User?

    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    fun getByUsername(username: String): User

    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    fun findByEmail(email: String): User?

    @Query("select u from User u join PasswordResetKey key on key.user = u where key.passwordResetKey = ?1")
    fun findByPasswordResetKeyValue(passwordResetKeyValue: String): User?

}
