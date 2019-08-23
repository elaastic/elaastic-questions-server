package org.elaastic.questions.lti

import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat

@SpringBootTest
@Transactional
internal class LmsAssignmentRepositoryIntegrationTest(
        @Autowired val entityManager: EntityManager,
        @Autowired val testingService: TestingService,
        @Autowired val lmsAssignmentRepository: LmsAssignmentRepository
) {

    @Test
    fun `test save of a valid lti user`() {
        tGiven {
            // a valid lms user
            LmsAssignment(
                    testingService.getAnyLtiConsumer(),
                    testingService.getAnyLtiContext().lmsActivityId,
                    testingService.getAnyLtiContext().lmsCourseId,
                    testingService.getAnyAssignment()
            ).tWhen {
                // saving the lms user
                lmsAssignmentRepository.saveAndFlush(it)
            }.tThen {
                entityManager.refresh(it)
                assertThat(it.id, notNullValue())
                assertThat(it.assignment, equalTo(testingService.getAnyAssignment()))
                assertThat(it.lmsActivityId, equalTo(testingService.getAnyLtiContext().lmsActivityId))
                assertThat(it.lms, equalTo(testingService.getAnyLtiConsumer()))
            }
        }

    }


}
