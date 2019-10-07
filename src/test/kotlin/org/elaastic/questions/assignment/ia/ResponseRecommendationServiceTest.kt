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

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.junit.jupiter.api.Test
import com.nhaarman.mockitokotlin2.*

internal class ResponseRecommendationServiceTest {

    @Test
    fun `Simple test of computeRecommendations`() {
        val service = ResponseRecommendationService()

        service.computeRecommendations(
                listOf(
                        mock<Response> {
                            on { score }.doReturn(100f)
                            on { id }.doReturn(1)
                            on { explanation }.doReturn("Hello World, and Universe")
                        },
                        mock<Response> {
                            on { score }.doReturn(50f)
                            on { id }.doReturn(2)
                            on { explanation }.doReturn("Hello World, and Universe")
                        },
                        mock<Response> {
                            on { score }.doReturn(100f)
                            on { id }.doReturn(3)
                            on { explanation }.doReturn("Hello World, and Universe")
                        },
                        mock<Response> {
                            on { score }.doReturn(100f)
                            on { id }.doReturn(4)
                            on { explanation }.doReturn("Hello World, and Universe")
                        },
                        mock<Response> {
                            on { score }.doReturn(50f)
                            on { id }.doReturn(5)
                            on { explanation }.doReturn("Hello World, and Universe")
                        },
                        mock<Response> {
                            on { score }.doReturn(100f)
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