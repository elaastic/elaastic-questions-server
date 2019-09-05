package org.elaastic.questions.lti

import org.elaastic.questions.lti.controller.LtiLaunchData
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import javax.transaction.Transactional

@SpringBootTest
@Transactional
internal class LmsServiceIntegrationTest(
        @Autowired val lmsService: LmsService,
        @Autowired val testingService: TestingService
) {

    lateinit var ltiData: LtiLaunchData
    lateinit var ltiDataWithBadGlobalId: LtiLaunchData

    @BeforeEach
    fun setup() {
        ltiData = testingService.getLtiLaunchDataComingFromBoBDeniroTeacher()
        ltiDataWithBadGlobalId = testingService.getLtiLaunchDataWithBadGlobalId()
    }

    @Test
    fun testGetLmsUser() {
        tGiven("lti data") {
            ltiData
        }.tWhen("Trying to get the corresponding lms user") {
            lmsService.getLmsUser(ltiData.toLtiUser())
        }.tThen("lms user is obtained with its corresponding user") {
            assertThat(it.id, notNullValue())
            assertThat(it.user, notNullValue())
            assertThat(it.lms, equalTo(testingService.getAnyLtiConsumer()))
            assertThat(it.user.lastName, equalTo("Deniro"))
            assertTrue(it.user.isTeacher())
            it
        }.tWhen("the lms user is asked with same lti data") {
            lmsService.getLmsUser(ltiData.toLtiUser()).tThen("the same user is obtained") { newLmsUser ->
                assertThat(newLmsUser, equalTo(it))
                assertThat(newLmsUser.user, equalTo(it.user))
            }
        }
    }

    @Test
    fun testGetLmsAssignment() {
        tGiven("lti data and corresponding lms user") {
            lmsService.getLmsUser(ltiData.toLtiUser())
        }.tWhen("assignment is asked") {
            lmsService.getLmsAssignment(
                    lmsUser = it,
                    ltiActivity = ltiData.toLtiActivity()
            )
        }.tThen("a new assignment is created and returned") {
            assertThat(it.lms, equalTo(testingService.getAnyLtiConsumer()))
            assertThat(it.lmsActivityId, equalTo(ltiData.resource_link_id))
            assertThat(it.lmsCourseId, equalTo(ltiData.context_id))
            assertThat(it.assignment.owner, equalTo(lmsService.getLmsUser(ltiData.toLtiUser()).user))
            assertThat(it.lmsCourseTitle, equalTo(ltiData.context_title))
            assertThat(it.assignment.title, equalTo(ltiData.resource_link_title))
            it
        }.tWhen("assignment with same lti data is asked") {
            lmsService.getLmsUser(ltiData.toLtiUser()).let { lmsuser ->
                lmsService.getLmsAssignment(
                        lmsUser = lmsuser,
                        ltiActivity = ltiData.toLtiActivity()
                ).tThen { newLmsAssignment ->
                    assertThat(newLmsAssignment, equalTo(it))
                    assertThat(newLmsAssignment.assignment, equalTo(it.assignment))
                }
            }
        }
        tWhen("lti data specify a globalid that is not corresponding to an existing assignment") {
            lmsService.getLmsUser(ltiDataWithBadGlobalId.toLtiUser())
        }.tExpect("Esception is thrown") {
            assertThrows<IllegalArgumentException> {
                lmsService.getLmsAssignment(
                        lmsUser = it,
                        ltiActivity = ltiDataWithBadGlobalId.toLtiActivity()
                )
            }
        }
    }
}
