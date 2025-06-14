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

package org.elaastic.user

import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.user.Role.RoleId
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `test hasRole with one role`() {
        tGiven("a user with the STUDENT role") {
            user("John", "Doe")
                .also {
                    it.roles.clear()
                    it.roles.add(Role(RoleId.STUDENT))
                }
        }.tThen {
            assertTrue(it.isLearner(), "User should be a learner")
            assertFalse(it.isTeacher(), "User should not be a teacher")
            assertFalse(it.isAdmin(), "User should not be an admin")
            assertTrue(it hasRole RoleId.STUDENT, "User should have STUDENT role")
            assertFalse(it hasRole RoleId.TEACHER, "User should not have TEACHER role")
            assertFalse(it hasRole RoleId.ADMIN, "User should not have ADMIN role")
        }

        tGiven("a user with the TEACHER role") {
            user("Jane", "Doe")
                .also {
                    it.roles.clear()
                    it.roles.add(Role(RoleId.TEACHER))
                }
        }.tThen {
            assertFalse(it.isLearner(), "User should not be a learner")
            assertTrue(it.isTeacher(), "User should be a teacher")
            assertFalse(it.isAdmin(), "User should not be an admin")
            assertFalse(it hasRole RoleId.STUDENT, "User should not have STUDENT role")
            assertTrue(it hasRole RoleId.TEACHER, "User should have TEACHER role")
            assertFalse(it hasRole RoleId.ADMIN, "User should not have ADMIN role")
        }

        tGiven("a user with th ADMIN role") {
            user("Admin", "User")
                .also {
                    it.roles.clear()
                    it.roles.add(Role(RoleId.ADMIN))
                }
        }.tThen {
            assertFalse(it.isLearner(), "User should not be a learner")
            assertFalse(it.isTeacher(), "User should not be a teacher")
            assertTrue(it.isAdmin(), "User should be an admin")
            assertFalse(it hasRole RoleId.STUDENT, "User should not have STUDENT role")
            assertFalse(it hasRole RoleId.TEACHER, "User should not have TEACHER role")
            assertTrue(it hasRole RoleId.ADMIN, "User should have ADMIN role")
        }
    }

    @Test
    fun `test hasRole with multiple role`() {
        tGiven("a user with multiple roles") {
            user("John", "Doe")
                .also {
                    it.roles.clear()
                    it.roles.add(Role(RoleId.STUDENT))
                    it.roles.add(Role(RoleId.TEACHER))
                }
        }.tThen {
            assertTrue(it.isLearner(), "User should be a learner")
            assertTrue(it.isTeacher(), "User should be a teacher")
            assertFalse(it.isAdmin(), "User should not be an admin")
            assertTrue(it hasRole RoleId.STUDENT, "User should have STUDENT role")
            assertTrue(it hasRole RoleId.TEACHER, "User should have TEACHER role")
            assertFalse(it hasRole RoleId.ADMIN, "User should not have ADMIN role")
        }
    }

    @Test
    fun `test hasRole with no role`() {
        tGiven("a user with no roles") {
            user("John", "Doe")
        }.tThen {
            assertTrue(it.roles.isEmpty())
            assertFalse(it.isLearner(), "User should not be a learner")
            assertFalse(it.isTeacher(), "User should not be a teacher")
            assertFalse(it.isAdmin(), "User should not be an admin")
            assertFalse(it hasRole RoleId.STUDENT, "User should not have STUDENT role")
            assertFalse(it hasRole RoleId.TEACHER, "User should not have TEACHER role")
            assertFalse(it hasRole RoleId.ADMIN, "User should not have ADMIN role")
        }
    }

    private fun user(firstName: String, lastName: String, password: String = "1234") = User(
        firstName = firstName,
        lastName = lastName,
        username = "$firstName.$lastName",
        plainTextPassword = password
    )
}