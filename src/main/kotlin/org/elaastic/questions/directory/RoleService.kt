package org.elaastic.questions.directory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

/**
 * @author John Tranier
 */
@Service
class RoleService(
        @Autowired val roleRepository: RoleRepository
) {

    // will be initialized by loadAllRoleId
    companion object cache {
        var STUDENT_ROLE_ID: Long = -1
        var TEACHER_ROLE_ID: Long = -1
        var ADMIN_ROLE_ID: Long = -1
    }


    fun roleStudent(): Role {
        return roleRepository.getOne(STUDENT_ROLE_ID)!!
    }

    fun roleTeacher(): Role {
        return roleRepository.getOne(TEACHER_ROLE_ID)!!
    }

    fun roleAdmin(): Role {
        return roleRepository.getOne(ADMIN_ROLE_ID)!!
    }

    @PostConstruct
    private fun loadAllRoleId() {

        val allRole = roleRepository.findAll().associateBy({ it.name }, { it })

        // Load IDs
        STUDENT_ROLE_ID = allRole[Role.RoleId.STUDENT.roleName]!!.id!!
        TEACHER_ROLE_ID = allRole[Role.RoleId.TEACHER.roleName]!!.id!!
        ADMIN_ROLE_ID = allRole[Role.RoleId.ADMIN.roleName]!!.id!!

    }
}