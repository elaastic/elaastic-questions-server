package org.elaastic.questions.directory

import org.springframework.data.repository.CrudRepository


interface RoleRepository : CrudRepository<Role, Long> {

    fun getOne(id: Long): Role?
    fun findByName(name: String): Role?

}