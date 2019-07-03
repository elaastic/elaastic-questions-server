package org.elaastic.questions.directory

import org.springframework.data.repository.CrudRepository

/**
 * @author John Tranier
 */
interface UserRepository : CrudRepository<User, Long> {

    fun findByUsername(username: String): User?
}