/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.directory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.persistence.EntityManager
import javax.transaction.Transactional


@Service
class RoleService(
        @Autowired val roleRepository: RoleRepository,
        @Autowired val entityManager: EntityManager
) {

    private lateinit var allRole: Map<String, Role>

    // will be initialized by loadAllRoleId
    companion object Cache {
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

    @Transactional
    fun roleForName(name: String, attachedToCurrentTransaction: Boolean = false): Role {
        when(attachedToCurrentTransaction) {
            false -> return allRole[name]!!
            true -> return entityManager.merge(allRole[name]!!)
        }

    }

    @PostConstruct
    private fun loadAllRoleId() {
        allRole = roleRepository.findAll().associateBy({ it.name }, { it })
        // Load IDs
        STUDENT_ROLE_ID = allRole[Role.RoleId.STUDENT.roleName]!!.id!!
        TEACHER_ROLE_ID = allRole[Role.RoleId.TEACHER.roleName]!!.id!!
        ADMIN_ROLE_ID = allRole[Role.RoleId.ADMIN.roleName]!!.id!!

    }
}
