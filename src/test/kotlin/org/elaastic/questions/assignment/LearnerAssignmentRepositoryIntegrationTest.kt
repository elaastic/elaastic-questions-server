package org.elaastic.questions.assignment

import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import javax.persistence.EntityManager


/**
 * @author John Tranier
 */
@SpringBootTest
@Transactional
class LearnerAssignmentRepositoryIntegrationTest(
        @Autowired val learnerAssignmentRepository: LearnerAssignmentRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid learner assignment`() {
        val learner = testingService.getAnyUser()
        val assignment = testingService.getAnyAssignment()
        LearnerAssignment(
                learner = learner,
                assignment = assignment
        ).tWhen {
            learnerAssignmentRepository.saveAndFlush(it)
            entityManager.refresh(it)
            it
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(it.version, equalTo(0L))
            assertThat(it.dateCreated, notNullValue())
            assertThat(it.lastUpdated, notNullValue())
            assertThat(it.learner, equalTo(learner))
            assertThat(it.assignment, equalTo(assignment))
        }
    }
}
