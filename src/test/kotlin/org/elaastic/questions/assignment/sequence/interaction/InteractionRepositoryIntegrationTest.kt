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

package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.specification.EvaluationSpecification
import org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*


@SpringBootTest
@Transactional
internal class InteractionRepositoryIntegrationTest(
        @Autowired val interactionRepository: InteractionRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid interaction - without specification`() {
        val owner = testingService.getAnyUser()
        val sequence = testingService.getAnySequence()
        Interaction(
                interactionType = InteractionType.Read,
                rank = 1,
                owner = owner,
                sequence = sequence
        )
                .tWhen {
                    interactionRepository.saveAndFlush(it)
                    entityManager.refresh(it)
                    it
                }
                .tThen {
                    assertThat(it.id, notNullValue())
                    assertThat(it.version, equalTo(0L))
                    assertThat(it.dateCreated, notNullValue())
                    assertThat(it.lastUpdated, notNullValue())
                    assertThat(it.rank, equalTo(1))
                    assertThat(it.interactionType, equalTo(InteractionType.Read))
                    assertThat(it.owner, equalTo(owner))
                    assertThat(it.sequence, equalTo(sequence))
                    assertThat(it.state, equalTo(State.beforeStart))
                    assertThat(it.specification, nullValue())
                }
    }

    @Test
    fun `save a valid interaction - with evaluation specification`() {
        val owner = testingService.getAnyUser()
        val sequence = testingService.getAnySequence()
        val specification = EvaluationSpecification(
                responseToEvaluateCount = 3
        )
        Interaction(
                interactionType = InteractionType.Evaluation,
                rank = 1,
                owner = owner,
                sequence = sequence,
                specification = specification
        )
                .tWhen {
                    interactionRepository.saveAndFlush(it)
                    entityManager.refresh(it)
                    it
                }
                .tThen {
                    assertThat(it.specification as EvaluationSpecification, equalTo(specification))
                }
    }

    @Test
    fun `save a valid interaction - with response submission specification`() {
        val owner = testingService.getAnyUser()
        val sequence = testingService.getAnySequence()
        val specification = ResponseSubmissionSpecification(
                studentsProvideConfidenceDegree = true,
                studentsProvideExplanation = false
        )
        Interaction(
                interactionType = InteractionType.ResponseSubmission,
                rank = 1,
                owner = owner,
                sequence = sequence,
                specification = specification
        )
                .tWhen {
                    interactionRepository.saveAndFlush(it)
                    entityManager.refresh(it)
                    it
                }
                .tThen {
                    assertThat(
                            it.specification as ResponseSubmissionSpecification,
                            equalTo(specification)
                    )
                }
    }

    @Test
    fun `check ExplanationRecommendationMapping persistence`() {
        // Given :
        val explanationRecommendationMapping =
                ExplanationRecommendationMapping(
                        mapOf(
                                "1" to listOf(1L,2L,3L,5L),
                                "3" to listOf(4L, 128L)
                        )
                )
        val owner = testingService.getAnyUser()
        val sequence = testingService.getAnySequence()
        Interaction(
                interactionType = InteractionType.Evaluation,
                rank = 1,
                owner = owner,
                sequence = sequence
        )
                .tWhen {
                    it.explanationRecommendationMapping = explanationRecommendationMapping
                    interactionRepository.saveAndFlush(it)
                    entityManager.refresh(it)
                    it
                }
                .tThen {
                    assertThat(it.explanationRecommendationMapping, equalTo(explanationRecommendationMapping))
                }
    }
}
