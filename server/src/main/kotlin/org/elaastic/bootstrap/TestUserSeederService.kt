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

import org.elaastic.user.Role
import org.elaastic.user.RoleService
import org.elaastic.user.User
import org.elaastic.user.UserService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class TestUserSeederService(
    val userService: UserService,
    val roleService: RoleService
) {

    private data class TestAccount(
        val firstName: String,
        val lastName: String,
        val role: Role,
        val username: String,
        val email: String = "$username@elaastic.org"
    )

    @Transactional
    fun initializeDevUsers() {
        val teacherRole = roleService.roleTeacher()
        val studentRole = roleService.roleStudent()
        val adminRole = roleService.roleAdmin()

        listOf(
            TestAccount("Franck", "Sil", teacherRole, "fsil"),
            TestAccount("Rialy", "Andria", teacherRole, "rand"),
            TestAccount("Albert", "Ein", teacherRole, "aein"),
            TestAccount("Mary", "Sil", studentRole, "msil"),
            TestAccount("Thom", "Sil", studentRole, "tsil"),
            TestAccount("John", "Tra", studentRole, "jtra"),
            TestAccount("Erik", "Erik", studentRole, "erik"),
            TestAccount("admin", "root", adminRole, "rootadmin", email = "admin@elaastic.org")
        ).forEach { account ->
            userService.findByUsername(account.username) ?: userService.addUser(createUser(account))
        }
    }

    private fun createUser(account: TestAccount): User =
        User(
            firstName = account.firstName,
            lastName = account.lastName,
            username = account.username,
            plainTextPassword = "1234",
            email = account.email
        ).addRole(account.role)
}
