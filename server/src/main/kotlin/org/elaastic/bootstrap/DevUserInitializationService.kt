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

package org.elaastic.bootstrap

import org.elaastic.user.RoleService
import org.elaastic.user.User
import org.elaastic.user.UserService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class DevUserInitializationService(
    val userService: UserService,
    val roleService: RoleService
) {

    @Transactional
    fun initializeDevUsers() {
        listOf(
            User(
                firstName = "Franck",
                lastName = "Sil",
                username = "fsil",
                plainTextPassword = "1234",
                email = "fsil@elaastic.org"
            ).addRole(roleService.roleTeacher()),
            User(
                firstName = "Rialy",
                lastName = "Andria",
                username = "rand",
                plainTextPassword = "1234",
                email = "rand@elaastic.org"
            ).addRole(roleService.roleTeacher()),
            User(
                firstName = "Albert",
                lastName = "Ein",
                username = "aein",
                plainTextPassword = "1234",
                email = "aein@elaastic.org"
            ).addRole(roleService.roleTeacher()),
            User(
                firstName = "Mary",
                lastName = "Sil",
                username = "msil",
                plainTextPassword = "1234",
                email = "msil@elaastic.org"
            ).addRole(roleService.roleStudent()),
            User(
                firstName = "Thom",
                lastName = "Sil",
                username = "tsil",
                plainTextPassword = "1234",
                email = "tsil@elaastic.org"
            ).addRole(roleService.roleStudent()),
            User(
                firstName = "John",
                lastName = "Tra",
                username = "jtra",
                plainTextPassword = "1234",
                email = "jtra@elaastic.org"
            ).addRole(roleService.roleStudent()),
            User(
                firstName = "Erik",
                lastName = "Erik",
                username = "erik",
                plainTextPassword = "1234",
                email = "erik@elaastic.org"
            ).addRole(roleService.roleStudent()),
            User(
                firstName = "admin",
                lastName = "root",
                username = "rootadmin",
                plainTextPassword = "1234",
                email = "admin@elaastic.org"
            ).addRole(roleService.roleAdmin())
        ).forEach {
            userService.findByUsername(it.username) ?: userService.addUser(it)
        }
    }

}
