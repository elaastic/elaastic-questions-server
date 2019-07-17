package org.elaastic.questions.directory

import org.springframework.data.repository.CrudRepository

/**
 * @author John Tranier
 */
interface RoleRepository : CrudRepository<Role, Long> {

    fun getOne(id: Long): Role?
    fun findByName(name: String): Role?

}