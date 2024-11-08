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
package org.elaastic.questions.assignment.ia

import org.elaastic.activity.response.Response
import org.junit.jupiter.api.Test
import com.nhaarman.mockitokotlin2.*
import org.elaastic.sequence.interaction.ResponseId
import org.elaastic.activity.response.ResponseRepository
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
                print(responseToString(responseMap[(i+1).toLong()]!!))
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
}
