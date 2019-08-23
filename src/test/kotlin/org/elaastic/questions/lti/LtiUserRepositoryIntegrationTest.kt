package org.elaastic.questions.lti

import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat

@SpringBootTest
@Transactional
internal class LtiUserRepositoryIntegrationTest(
        @Autowired val entityManager: EntityManager,
        @Autowired val testingService: TestingService,
        @Autowired val ltiUserRepository: LtiUserRepository
) {

    @Test
    fun `test save of a valid lti user`() {
        tGiven {
            // a valid lti user
            LtiUser.LtiUserId(
                    testingService.getAnyLtiContext().ltiContextId.lmsKey,
                    testingService.getAnyLtiContext().ltiContextId.lmsActivityId,
                    "alPacino"
            ).let {
                LtiUser(
                        it,
                        testingService.getAnyLtiConsumer(),
                        testingService.getAnyLtiContext(),
                        it.lmsUserId
                )
            }.tWhen {
                // saving the user
                ltiUserRepository.saveAndFlush(it)
            }.tThen {
                // properties are set as expected
                entityManager.refresh(it)
                assertThat(it.lmsActivity, equalTo(testingService.getAnyLtiContext()))
                assertThat(it.lms, equalTo(testingService.getAnyLtiConsumer()))
                assertThat(it.lmsUserId, equalTo("alPacino"))
                assertThat(it.ltiUserId.lmsUserId, equalTo(it.lmsUserId))
                assertThat(it.ltiUserId.lmsActivityId, equalTo(it.lmsActivity.lmsActivityId))
                assertThat(it.ltiUserId.lmsKey, equalTo(it.lms.key))
            }
        }

    }


}
