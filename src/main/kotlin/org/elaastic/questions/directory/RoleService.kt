package org.elaastic.questions.directory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 * @author John Tranier
 */
@Service
// @CacheConfig(cacheNames = arrayOf("roles"))
class RoleService(
        @Autowired val roleRepository: RoleRepository
) {

    fun roleStudent(): Role {
        return getAllRole()[Role.RoleId.STUDENT]!!
    }

    fun roleTeacher(): Role {
        return getAllRole()[Role.RoleId.TEACHER]!!
    }

    fun roleAdmin(): Role {
        return getAllRole()[Role.RoleId.ADMIN]!!
    }

    // @Cacheable
    fun getAllRole(): Map<Role.RoleId, Role> {

        val allRole = roleRepository.findAll().associateBy({ it.name }, { it })

        return mapOf(
                Role.RoleId.STUDENT to allRole[Role.RoleId.STUDENT.roleName]!!,
                Role.RoleId.TEACHER to allRole[org.elaastic.questions.directory.Role.RoleId.TEACHER.roleName]!!,
                Role.RoleId.ADMIN to allRole[org.elaastic.questions.directory.Role.RoleId.ADMIN.roleName]!!
        )


    }
}