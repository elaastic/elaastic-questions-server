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
package org.elaastic.activity.evaluation.peergrading

import com.nhaarman.mockitokotlin2.*
import org.elaastic.activity.evaluation.ResponseId
import org.elaastic.activity.response.Response
import org.elaastic.activity.response.ResponseRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import javax.persistence.EntityManager


internal class ResponseRecommendationServiceTest {

    companion object {
        fun responseToString(response: Response): String {
            val correct = response.score?.compareTo(BigDecimal(100)) == 0
            return "#${response.id}(${correct})"
        }
    }

    @Test
    fun `Simple test of computeRecommendations`() {
        val service = ResponseRecommendationService(
            mock<EntityManager>(),
            mock<ResponseRepository>()
        )

        val responseMap = mapOf<ResponseId, Response>(
            1L to mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { id }.doReturn(1)
                on { explanation }.doReturn("Hello World, and Universe")
            },
            2L to mock<Response> {
                on { score }.doReturn(BigDecimal(50))
                on { id }.doReturn(2)
                on { explanation }.doReturn("Hello World, and Universe")
            },
            3L to mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { id }.doReturn(3)
                on { explanation }.doReturn("Hello World, and Universe")
            },
            4L to mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { id }.doReturn(4)
                on { explanation }.doReturn("Hello World, and Universe")
            },
            5L to mock<Response> {
                on { score }.doReturn(BigDecimal(50))
                on { id }.doReturn(5)
                on { explanation }.doReturn("Hello World, and Universe")
            },
            6L to mock<Response> {
                on { score }.doReturn(BigDecimal(100))
                on { id }.doReturn(6)
                on { explanation }.doReturn("Hello World, and Universe")
            },
            7L to mock<Response> {
                on { score }.doReturn(null)
                on { id }.doReturn(7)
                on { explanation }.doReturn("Hello World, and Universe")
            },
        )

        service.computeRecommendations(
            responseMap.keys.map { responseMap[it]!! },
            3
        ).let { explanationRecommendationMapping ->
            val nbSelectionMap = Array<Int>(responseMap.keys.size) { 0 }

            responseMap.keys.forEach { id ->
                print(responseToString(responseMap[id]!!))
                print(" ==> ")
                explanationRecommendationMapping[id].map {
                    nbSelectionMap[it.toInt() - 1]++
                    print(responseToString(responseMap[it]!!))
                    print(", ")
                }
                println()
            }

            nbSelectionMap.forEachIndexed() { i, nb ->
                print(responseToString(responseMap[(i + 1).toLong()]!!))
                print(" ==> ")
                println(nb)
            }

        }
    }

    @Test
    fun `test of computeRecommendations with incorrect answers only`() {
        val service = ResponseRecommendationService(
            mock<EntityManager>(),
            mock<ResponseRepository>()
        )

        service.computeRecommendations(
            listOf(
                mock<Response> {
                    on { score }.doReturn(BigDecimal(50))
                    on { id }.doReturn(1)
                    on { explanation }.doReturn("Hello World, and Universe")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(50))
                    on { id }.doReturn(2)
                    on { explanation }.doReturn("Hello World, and Universe")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(50))
                    on { id }.doReturn(3)
                    on { explanation }.doReturn("Hello World, and Universe")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(50))
                    on { id }.doReturn(4)
                    on { explanation }.doReturn("Hello World, and Universe")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(50))
                    on { id }.doReturn(5)
                    on { explanation }.doReturn("Hello World, and Universe")
                },
                mock<Response> {
                    on { score }.doReturn(BigDecimal(50))
                    on { id }.doReturn(6)
                    on { explanation }.doReturn("Hello World, and Universe")
                }
            ),
            3
        ).let {
            print(it)
        }
    }

    @Test
    fun `test CORRECT_RESPONSE_FIRST`() {
        // Same Object
        val responseInfo1T0 = ResponseInfo(1, true, nbSelection = 0)
        assertSame(responseInfo1T0, responseInfo1T0)
        assertEquals(
            0, CORRECT_RESPONSE_FIRST.compare(responseInfo1T0, responseInfo1T0),
            "As the two objects are the same, CORRECT_RESPONSE_FIRST should return 0"
        )
        val another1T0 = ResponseInfo(1, true, nbSelection = 0)
        assertEquals(
            0, CORRECT_RESPONSE_FIRST.compare(another1T0, another1T0),
            "As the two objects are the same, CORRECT_RESPONSE_FIRST should return 0"
        )

        // Different on the correctness
        val responseInfo1F0 = ResponseInfo(1, false, nbSelection = 0)
        assertEquals(
            1, CORRECT_RESPONSE_FIRST.compare(responseInfo1T0, responseInfo1F0),
            "CORRECT_RESPONSE_FIRST should sort responseInfo1T0 before responseInfo1F0"
        )
        assertEquals(
            -1, CORRECT_RESPONSE_FIRST.compare(responseInfo1F0, responseInfo1T0),
            "CORRECT_RESPONSE_FIRST should sort responseInfo1T0 before responseInfo1F0"
        )

        // Different on the nbSelection
        val responseInfo1T1 = ResponseInfo(1, true, nbSelection = 1)
        assertEquals(
            1, CORRECT_RESPONSE_FIRST.compare(responseInfo1T0, responseInfo1T1),
            "CORRECT_RESPONSE_FIRST should sort responseInfo1T0 before responseInfo1T1"
        )
        assertEquals(
            -1, CORRECT_RESPONSE_FIRST.compare(responseInfo1T1, responseInfo1T0),
            "CORRECT_RESPONSE_FIRST should sort responseInfo1T0 before responseInfo1T1"
        )

        // Different on the id
        val responseInfo2T0 = ResponseInfo(2, true, nbSelection = 0)
        assertEquals(
            1, CORRECT_RESPONSE_FIRST.compare(responseInfo1T0, responseInfo2T0),
            "CORRECT_RESPONSE_FIRST should sort responseInfo1T0 before responseInfo2T0"
        )
        assertEquals(
            -1, CORRECT_RESPONSE_FIRST.compare(responseInfo2T0, responseInfo1T0),
            "CORRECT_RESPONSE_FIRST should sort responseInfo1T0 before responseInfo2T0"
        )
    }

    @Test
    fun `test INCORRECT_RESPONSE_FIRST`() {
        // Same Object
        val responseInfo1T0 = ResponseInfo(1, true, nbSelection = 0)
        assertSame(responseInfo1T0, responseInfo1T0)
        assertEquals(
            0, INCORRECT_RESPONSE_FIRST.compare(responseInfo1T0, responseInfo1T0),
            "As the two objects are the same, INCORRECT_RESPONSE_FIRST should return 0"
        )
        val another1T0 = ResponseInfo(1, true, nbSelection = 0)
        assertEquals(
            0, INCORRECT_RESPONSE_FIRST.compare(another1T0, another1T0),
            "As the two objects are the same, INCORRECT_RESPONSE_FIRST should return 0"
        )

        // Different on the correctness
        val responseInfo1F0 = ResponseInfo(1, false, nbSelection = 0)
        assertEquals(
            -1, INCORRECT_RESPONSE_FIRST.compare(responseInfo1T0, responseInfo1F0),
            "INCORRECT_RESPONSE_FIRST should sort responseInfo1F0 before responseInfo1T0"
        )
        assertEquals(
            1, INCORRECT_RESPONSE_FIRST.compare(responseInfo1F0, responseInfo1T0),
            "INCORRECT_RESPONSE_FIRST should sort responseInfo1F0 before responseInfo1T0"
        )

        // Different on the nbSelection
        val responseInfo1T1 = ResponseInfo(1, true, nbSelection = 1)
        assertEquals(
            1, INCORRECT_RESPONSE_FIRST.compare(responseInfo1T0, responseInfo1T1),
            "INCORRECT_RESPONSE_FIRST should sort responseInfo1T1 before responseInfo1T0"
        )
        assertEquals(
            -1, INCORRECT_RESPONSE_FIRST.compare(responseInfo1T1, responseInfo1T0),
            "INCORRECT_RESPONSE_FIRST should sort responseInfo1T1 before responseInfo1T0"
        )

        // Different on the id
        val responseInfo2T0 = ResponseInfo(2, true, nbSelection = 0)
        assertEquals(
            1, INCORRECT_RESPONSE_FIRST.compare(responseInfo1T0, responseInfo2T0),
            "INCORRECT_RESPONSE_FIRST should sort responseInfo2T0 before responseInfo1T0"
        )
        assertEquals(
            -1, INCORRECT_RESPONSE_FIRST.compare(responseInfo2T0, responseInfo1T0),
            "INCORRECT_RESPONSE_FIRST should sort responseInfo2T0 before responseInfo1T0"
        )
    }
}
