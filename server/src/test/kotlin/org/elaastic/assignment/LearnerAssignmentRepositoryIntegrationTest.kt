package org.elaastic.assignment

import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class LearnerAssignmentRepositoryIntegrationTest(
    @Autowired val learnerAssignmentRepository: LearnerAssignmentRepository,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid learner assignment`() {
        val learner = integrationTestingService.getAnyUser()
        val assignment = integrationTestingService.getAnyAssignment()
        LearnerAssignment(
            learner = learner,
            assignment = assignment
        ).tWhen {
            learnerAssignmentRepository.saveAndFlush(it)
            entityManager.refresh(it)
            it
        }.tThen {
            MatcherAssert.assertThat(it.id, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(0L))
            MatcherAssert.assertThat(it.dateCreated, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(it.lastUpdated, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(it.learner, CoreMatchers.equalTo(learner))
            MatcherAssert.assertThat(it.assignment, CoreMatchers.equalTo(assignment))
        }
    }
}