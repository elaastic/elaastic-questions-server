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

package org.elaastic.sequence.interaction

import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.sequence.interaction.response.Response
import org.elaastic.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import java.math.BigDecimal


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ResponseRepositoryIntegrationTest(
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid interaction response - with minimal data`() {
        val learner = integrationTestingService.getAnyUser()
        val interaction = integrationTestingService.getAnyInteraction()
        Response(
                learner = learner,
                interaction = interaction,
                statement = interaction.sequence.statement
        ).tWhen {
            responseRepository.saveAndFlush(it)
            entityManager.refresh(it)
            it
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(it.learner, equalTo(learner))
            assertThat(it.interaction, equalTo(interaction))
            assertThat(it.attempt, equalTo(1))
            assertThat(it.version, equalTo(0L))
            assertThat(it.dateCreated, notNullValue())
            assertThat(it.lastUpdated, notNullValue())
            assertThat(it.statement, notNullValue())
        }
    }

    @Test
    fun `save a valid interaction response - with all data`() {
        val learner = integrationTestingService.getAnyUser()
        val interaction = integrationTestingService.getAnyInteraction()
        val choiceListSpecification = LearnerChoice(listOf<Int>(1, 3))
        Response(
                learner = learner,
                interaction = interaction,
                attempt = 2,
                explanation = "explanation",
                confidenceDegree = ConfidenceDegree.CONFIDENT,
                meanGrade = BigDecimal(1),
                learnerChoice = choiceListSpecification,
                score = BigDecimal(2),
                statement = interaction.sequence.statement,
                recommendedByTeacher = true
        ).tWhen {
            responseRepository.saveAndFlush(it)
            entityManager.refresh(it)
            it
        }.tThen {
            assertThat(it.attempt, equalTo(2))
            assertThat(it.explanation, equalTo("explanation"))
            assertThat(it.confidenceDegree, equalTo(ConfidenceDegree.CONFIDENT))
            assertThat(it.meanGrade?.toDouble(), equalTo(BigDecimal(1).toDouble()))
            assertThat(it.learnerChoice, equalTo(choiceListSpecification))
            assertThat(it.score?.toDouble(), equalTo(BigDecimal(2).toDouble()))
            assertThat(it.recommendedByTeacher, equalTo(true))
        }
    }
}
