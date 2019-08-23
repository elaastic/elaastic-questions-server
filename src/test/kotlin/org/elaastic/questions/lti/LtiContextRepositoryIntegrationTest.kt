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
internal class LtiContextRepositoryIntegrationTest(
        @Autowired val ltiContextRepository: LtiContextRepository,
        @Autowired val entityManager: EntityManager,
        @Autowired val testingService: TestingService
) {

    @Test
    fun `test save of a valid lti context`() {
        tGiven {
            // a lti consumer
            testingService.getAnyLtiConsumer().let {
                // and building a context id
                LtiContext.LtiContextId(it.key, "a course").let { contextId ->
                    // and building a valid context
                    LtiContext(contextId, contextId.lmsActivityId,"course title", it, "courseId")
                }.tWhen { context ->
                    // saving the context
                    ltiContextRepository.saveAndFlush(context)
                }.tThen { context ->
                    entityManager.refresh(context)
                    // the context reference the lms as expected
                    assertThat(context.lms, equalTo(testingService.getAnyLtiConsumer()))
                    // and properties are set as expected
                    assertThat(context.lmsActivityId, equalTo("a course"))
                    assertThat(context.title, equalTo("course title"))
                    assertThat(context.ltiContextId.lmsKey, equalTo(it.key))
                    assertThat(context.dateCreated, notNullValue())
                    assertThat(context.lastUpdated, notNullValue())
                }

            }
        }

    }



}
