package org.elaastic.activity.response

import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.user.User
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class ResponseSetTest {

    private fun testResponse(attempt: Int, isFake: Boolean = false): Response {
        val user = User("user", "user", "user", "a")
        val statment = Statement(user, questionType = QuestionType.OpenEnded)
        val sequence = Sequence(user, statment)
        val interaction = Interaction(InteractionType.Read, attempt, owner = user, sequence = sequence)
        val response = Response(user, interaction, statement = statment, attempt = attempt, fake = isFake)
        return response
    }

    @Test
    fun `when no response isEmpty return true`() {
        val responseSet = ResponseSet(emptyList())
        assertTrue(responseSet.isEmpty())
    }

    @Test
    fun `when there is a response isEmpty return false`() {
        val responseSet = ResponseSet(listOf(testResponse(1)))

        assertFalse(responseSet.isEmpty())
    }

    @Test
    fun `when no response get with 1 or 2 return emptyList`() {
        val responseSet = ResponseSet(emptyList())

        assertTrue(responseSet[1].isEmpty())
        assertTrue(responseSet[2].isEmpty())
    }

    @Test
    fun `when there is a response on 1 attempt, get() with 1 return the response`() {
        val testResponse = testResponse(1)
        val responseSet = ResponseSet(listOf(testResponse))

        assertTrue(responseSet[1].isNotEmpty())
        assertEquals(testResponse, responseSet[1].first())

        assertTrue(responseSet[2].isEmpty())
    }

    @Test
    fun `when there is a response on 2 attempt, get() with 2 return the response`() {
        val testResponse = testResponse(2)
        val responseSet = ResponseSet(listOf(testResponse))

        assertTrue(responseSet[2].isNotEmpty())
        assertEquals(testResponse, responseSet[2].first())

        assertTrue(responseSet[1].isEmpty())
    }

    @Test
    fun `when there is two response on 1 & 2 attempt, get() with 1 return the first response`() {
        val testResponse = testResponse(1)
        val responseSet = ResponseSet(listOf(testResponse, testResponse(2)))

        assertTrue(responseSet[1].isNotEmpty())
        assertEquals(testResponse, responseSet[1].first())
        assertEquals(1, responseSet[1].size)

        assertTrue(responseSet[2].isNotEmpty())
    }

    @Test
    fun `throw an IllegalArgumentException when get() with somthing other than 1 or 2`() {
        val responseSet = ResponseSet(emptyList())

        assertThrows(IllegalArgumentException::class.java) {
            responseSet[-1]
        }
        assertThrows(IllegalArgumentException::class.java) {
            responseSet[0]
        }
        assertThrows(IllegalArgumentException::class.java) {
            responseSet[3]
        }
        assertThrows(IllegalArgumentException::class.java) {
            responseSet[4]
        }
    }

    @Test
    fun `when there is a fake response getWithoutFake() return an emptylist`() {
        val testResponse = testResponse(1, true)
        val responseSet = ResponseSet(listOf(testResponse))

        assertTrue(responseSet.getWithoutFake(1).isEmpty())
    }

    @Test
    fun `when there is a real response getWithoutFake() return the response`() {
        val testResponse = testResponse(1)
        val responseSet = ResponseSet(listOf(testResponse))

        assertTrue(responseSet.getWithoutFake(1).isNotEmpty())
        assertEquals(testResponse, responseSet.getWithoutFake(1).first())
    }

    @Test
    fun `when adding a response add() should'nt throw an Exception`() {
        val responseSet = ResponseSet(emptyList())

        assertDoesNotThrow {
            responseSet.add(testResponse(1))
            responseSet.add(testResponse(2))
        }
    }

    @Test
    fun `when adding a response with an attempt other than 1 or 2 add() should throw an Exception`() {
        val responseSet = ResponseSet(emptyList())

        assertThrows(IllegalStateException::class.java) {
            responseSet.add(testResponse(-1))
        }
        assertThrows(IllegalStateException::class.java) {
            responseSet.add(testResponse(0))
        }
        assertThrows(IllegalStateException::class.java) {
            responseSet.add(testResponse(3))
        }
        assertThrows(IllegalStateException::class.java) {
            responseSet.add(testResponse(4))
        }
    }
}