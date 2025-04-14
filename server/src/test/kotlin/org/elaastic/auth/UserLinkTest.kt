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