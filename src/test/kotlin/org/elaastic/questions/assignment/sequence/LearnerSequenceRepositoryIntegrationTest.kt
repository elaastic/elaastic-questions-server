package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManager


/**
 * @author John Tranier
 */
@SpringBootTest
@Transactional
@EnableJpaAuditing
internal class LearnerSequenceRepositoryIntegrationTest(
        @Autowired val learnerSequenceRepository: LearnerSequenceRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid learner sequence`() {
        val learner = testingService.getAnyUser()
        val sequence = testingService.getAnySequence()
        val interaction = testingService.getAnyInteraction()
        LearnerSequence(
                learner = learner,
                sequence = sequence,
                activeInteraction = interaction
        )
                .tWhen {
                    learnerSequenceRepository.saveAndFlush(it)
                    entityManager.refresh(it)
                    it
                }
                .tThen {
                    assertThat(it.id, notNullValue())
                    assertThat(it.version, equalTo(0L))
                    assertThat(it.dateCreated, notNullValue())
                    assertThat(it.lastUpdated, notNullValue())
                    assertThat(it.learner, equalTo(learner))
                    assertThat(it.sequence, equalTo(sequence))
                    assertThat(it.activeInteraction, equalTo(interaction))
                }
    }

}