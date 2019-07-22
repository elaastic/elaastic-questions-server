package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.sequence.Interaction
import org.elaastic.questions.assignment.sequence.InteractionRepository
import org.elaastic.questions.assignment.sequence.InteractionType
import org.elaastic.questions.assignment.sequence.State
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
internal class InteractionRepositoryIntegrationTest(
        @Autowired val interactionRepository: InteractionRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid interaction`() {
        val owner = testingService.getAnyUser()
        val sequence = testingService.getAnySequence()
        Interaction(
                interactionType = InteractionType.Evaluation,
                rank = 1,
                specification = "specification",
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
                    assertThat(it.interactionType, equalTo(InteractionType.Evaluation))
                    assertThat(it.owner, equalTo(owner))
                    assertThat(it.sequence, equalTo(sequence))
                    assertThat(it.state, equalTo(State.beforeStart))
                }
    }
}