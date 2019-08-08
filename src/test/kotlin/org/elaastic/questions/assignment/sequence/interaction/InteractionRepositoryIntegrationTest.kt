package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.specification.EvaluationSpecification
import org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.persistence.EntityManager
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*

/**
 * @author John Tranier
 */
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
    fun `check retrieving interaction result from test data`() {
        // Given
        interactionRepository.getOne(1688)
                .tExpect {
                    assertThat(
                            it.results,
                            equalTo(
                                    InteractionResult(
                                            OneAttemptResult(listOf(0f, 100f, 0f)),
                                            OneAttemptResult(listOf(0f, 93.939f, 6.061f))
                                    )
                            )
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
