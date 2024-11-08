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

package org.elaastic.material.instructional.question.explanation

import org.elaastic.questions.test.directive.*
import org.elaastic.questions.test.IntegrationTestingService
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManager
import javax.validation.ConstraintViolationException


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class FakeExplanationRepositoryIntegrationTest(
    @Autowired val fakeExplanationRepository: FakeExplanationRepository,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid fake explanation - with corresponding item`() {
        // Given a fake explanation
        val author = integrationTestingService.getAnyUser()
        val statement = integrationTestingService.getAnyStatement()
        FakeExplanation(
                content = "My fake explanation",
                author = author,
                correspondingItem = 3,
                statement = statement
        )
                // When saving the fake explanation
                .tWhen {
                    fakeExplanationRepository.saveAndFlush(it)
                    entityManager.refresh(it)
                    it
                }
                // Then the fake explanation should to be properly saved
                .tThen {
                    assertThat(it.id, not(nullValue()))
                    assertThat(it.author, equalTo(author))
                    assertThat(it.statement, equalTo(statement))
                }

    }

    @Test
    fun `save a valid fake explanation - without corresponding item`() {
        // Expect a fake explanation without corresponding item to be saved without error
        FakeExplanation(
                content = "My fake explanation",
                author = integrationTestingService.getAnyUser(),
                statement = integrationTestingService.getAnyStatement()
        )
                .tWhen {
                    fakeExplanationRepository.saveAndFlush(it)
                }
                .tNoProblem()
    }

    @Test
    fun `the content must be not blank`() {
        // Expect an error to be thrown when trying to save a fake explanation with blank content
        FakeExplanation(
                content = "",
                author = integrationTestingService.getAnyUser(),
                statement = integrationTestingService.getAnyStatement()
        )
                .tThen {
                    Assertions.assertThrows(ConstraintViolationException::class.java) {
                        fakeExplanationRepository.saveAndFlush(it)
                    }

                }

    }
}
