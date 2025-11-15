package org.elaastic.activity.evaluation.peergrading

import org.elaastic.activity.evaluation.peergrading.ResponseRecommendationService.Companion.CORRECT_RESPONSE_FIRST
import org.elaastic.activity.evaluation.peergrading.ResponseRecommendationService.Companion.INCORRECT_RESPONSE_FIRST
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils


class RecommendationResponsePoolTest {

    @Test
    fun `test when changing the comparator, the data remain in the set`() {
        val responseList = listOf(
            ResponseInfo(1, true),
            ResponseInfo(2, false),
            ResponseInfo(3, true)
        )
        val recommendationResponsePool = RecommendationResponsePool(
            responseList,
            INCORRECT_RESPONSE_FIRST
        )

        assertContentEquals(responseList, recommendationResponsePool.responseSet())

        // When we change the comparator
        recommendationResponsePool.comparator = CORRECT_RESPONSE_FIRST

        // Then the data in the set should remain the same
        assertContentEquals(responseList, recommendationResponsePool.responseSet())
    }

    @Test
    fun `next() should always return the same element when the comparator is the same`() {
        val responseList = listOf(
            ResponseInfo(1, true),
            ResponseInfo(2, false),
            ResponseInfo(3, true),
            ResponseInfo(4, false)
        )
        RecommendationResponsePool(
            responseList,
            INCORRECT_RESPONSE_FIRST
        ).let { recommendationResponsePool ->
            // When we call next() 100 times
            for (i in 0..100) {
                val next = recommendationResponsePool.next()
                assertNotNull(next)
                next as ResponseInfo
                assertFalse(
                    next.correct,
                    "As we are using the INCORRECT_RESPONSE_FIRST comparator, the next should always be false ResponseInfo"
                )
            }
        }

        RecommendationResponsePool(
            responseList,
            CORRECT_RESPONSE_FIRST
        ).let { recommendationResponsePool ->
            // When we call next() 100 times, we should always get the lat element of the set
            for (i in 0..100) {
                val next = recommendationResponsePool.next()
                assertNotNull(next)
                next as ResponseInfo
                assertTrue(
                    next.correct,
                    "As we are using the CORRECT_RESPONSE_FIRST comparator, the next should always be true ResponseInfo"
                )
            }
        }
    }

    @Test
    fun `next() should return null when the set is empty`() {
        val recommendationResponsePool = RecommendationResponsePool(
            emptyList(),
            INCORRECT_RESPONSE_FIRST
        )

        assertNull(recommendationResponsePool.next())
    }

    @Test
    fun `next() should return null when all elements are in the except list`() {
        val responseList = listOf(
            ResponseInfo(1, true),
            ResponseInfo(2, false),
            ResponseInfo(3, true)
        )
        val recommendationResponsePool = RecommendationResponsePool(
            responseList,
            INCORRECT_RESPONSE_FIRST
        )

        assertNull(recommendationResponsePool.next(responseList.map { it.id }))
    }

    @Test
    fun `next() should return the same element except if it's in the except list`() {
        val responseList = listOf(
            ResponseInfo(1, true),
            ResponseInfo(2, false),
            ResponseInfo(3, true)
        )
        // We create a comparator that sorts the elements by their id
        val comparator= Comparator<ResponseInfo> { o1, o2 -> o1.id.compareTo(o2.id) }

        val recommendationResponsePool = RecommendationResponsePool(
            responseList,
            comparator
        )

        // When we call next() 100 times
        for (i in 0..100) {
            val next = recommendationResponsePool.next()
            assertEquals(responseList.maxByOrNull { it.id }, next)
        }

        // When we call next() with the first element in the except list
        val except = listOf(responseList.maxOf { it.id })
        val next = recommendationResponsePool.next(except)
        // Then we should get the second element
        assertEquals(responseList.sortedByDescending { it.id }[1], next)
    }

    @Test
    fun `next() should increment the nbSelection of the selected element`() {
        val responseList = listOf(
            ResponseInfo(1, true)
        )
        val recommendationResponsePool = RecommendationResponsePool(
            responseList,
            INCORRECT_RESPONSE_FIRST
        )

        // When we call next() 100 times
        for (i in 0..100) {
            val next = recommendationResponsePool.next()
            assertNotNull(next)
            next as ResponseInfo
            assertEquals(i + 1, next.nbSelection)
        }
    }


    @Suppress("UNCHECKED_CAST")
    private fun RecommendationResponsePool.responseSet(): Set<ResponseInfo> {
        return ReflectionTestUtils.getField(this, "responseSet") as Set<ResponseInfo>
    }

    private fun assertContentEquals(
        expected: Collection<*>,
        actual: Collection<*>,
    ) {
        assertEquals(expected.size, actual.size)
        expected.forEach {
            assertTrue(actual.contains(it), "Expected $it to be in $actual")
        }
    }

}
