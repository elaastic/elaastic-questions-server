package org.elaastic.questions.directory

import org.springframework.data.repository.CrudRepository

/**
 * @author John Tranier
 */
interface RoleRepository : CrudRepository<Role, Long> {

    fun findByName(name: String): Role?

}