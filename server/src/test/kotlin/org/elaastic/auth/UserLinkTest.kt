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

package org.elaastic.auth

import org.elaastic.user.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserLinkTest {

    @Test
    fun testEquals() {
        val john1 = createUserLink("providerId", createUser("John", "Doe", "1234"))

        assertNotEquals(null, john1)
        assertNotEquals("", john1)
        assertSame(john1, john1)
        assertEquals(john1, john1)

        val john2 = createUserLink("providerId", createUser("John", "Doe", "1234"))

        assertNotSame(john1.user, john2.user)
        assertEquals(john1.user, john2.user)
        assertEquals(john1, john2)

        val jane = createUserLink("providerId", createUser("Jane", "Doe", "1234"))
        assertNotEquals(john1, jane)

        val john3 = createUserLink("anotherProviderId", createUser("John", "Doe", "1234"))
        assertEquals(john1.user, john3.user)
        assertNotEquals(john1, john3)
    }

    private fun createUserLink(providerId: String, user: User) = UserLink(
        providerId,
        user.username,
        user
    ).also { it.id = 2L }

    private fun createUser(firstName: String, lastName: String, password: String) = User(
        firstName = firstName,
        lastName = lastName,
        username = "$firstName.$lastName",
        plainTextPassword = password,
    ).also { it.id = 1L }


    @Test
    fun testHashCode() {
        val john1 = createUserLink("providerId", createUser("John", "Doe", "1234"))
        val john2 = createUserLink("providerId", createUser("John", "Doe", "1234"))

        assertNotSame(john1.user, john2.user)
        assertEquals(john1.hashCode(), john2.hashCode())

        val jane = createUserLink("providerId", createUser("Jane", "Doe", "1234"))
        assertNotEquals(john1.hashCode(), jane.hashCode())

        val john3 = createUserLink("anotherProviderId", john1.user)
        assertEquals(john1.user, john3.user)
        assertNotEquals(john1.hashCode(), john3.hashCode())
    }
}