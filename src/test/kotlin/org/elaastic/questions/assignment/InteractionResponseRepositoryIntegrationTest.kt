package org.elaastic.questions.assignment

import org.elaastic.questions.assignment.choice.ChoiceListSpecification
import org.elaastic.questions.test.TestingService
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
@EnableJpaAuditing
class InteractionResponseRepositoryIntegrationTest(
        @Autowired val interactionResponseRepository: InteractionResponseRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid interaction response - with minimal data`() {
        val learner = testingService.getAnyUser()
        val interaction = testingService.getAnyInteraction()
        InteractionResponse(
                learner = learner,
                interaction = interaction
        ).tWhen {
            interactionResponseRepository.saveAndFlush(it)
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
        }
    }

    @Test
    fun `save a valid interaction response - with all data`() {
        val learner = testingService.getAnyUser()
        val interaction = testingService.getAnyInteraction()
        val choiceListSpecification = ChoiceListSpecification(listOf<Int>(1, 3))
        InteractionResponse(
                learner = learner,
                interaction = interaction,
                attempt = 2,
                explanation = "explanation",
                confidenceDegree = 4,
                meanGrade = 1.0f,
                choiceListSpecification = choiceListSpecification,
                score = 2.0f
        ).tWhen {
            interactionResponseRepository.saveAndFlush(it)
            entityManager.refresh(it)
            it
        }.tThen {
            assertThat(it.attempt, equalTo(2))
            assertThat(it.explanation, equalTo("explanation"))
            assertThat(it.confidenceDegree, equalTo(4))
            assertThat(it.meanGrade, equalTo(1.0f))
            assertThat(it.choiceListSpecification, equalTo(choiceListSpecification))
            assertThat(it.score, equalTo(2.0f))
        }
    }
}