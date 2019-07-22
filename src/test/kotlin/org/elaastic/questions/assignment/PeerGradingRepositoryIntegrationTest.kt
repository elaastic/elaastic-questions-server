package org.elaastic.questions.assignment

import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManager
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*



/**
 * @author John Tranier
 */
@SpringBootTest
@Transactional
@EnableJpaAuditing
class PeerGradingRepositoryIntegrationTest(
        @Autowired val peerGradingRepository: PeerGradingRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid peer grading`() {
        val grader = testingService.getAnyUser()
        val interactionResponse = testingService.getAnyInteractionResponse()
        PeerGrading(
                grade = 2.0f,
                annotation = "Annotation",
                grader = grader,
                response = interactionResponse
        )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.refresh(it)
                    it
                }
                .tThen {
                    assertThat(it.id, notNullValue())
                    assertThat(it.version, equalTo(0L))
                    assertThat(it.dateCreated, notNullValue())
                    assertThat(it.lastUpdated, notNullValue())
                    assertThat(it.grader, equalTo(grader))
                    assertThat(it.response, equalTo(interactionResponse))
                }
    }
}