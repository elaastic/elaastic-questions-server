package org.elaastic.questions.assignment.sequence.peergrading.draxo

import org.elaastic.questions.course.CourseController
import org.elaastic.questions.security.TestSecurityConfig
import org.junit.Ignore
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@Ignore
@ExtendWith(SpringExtension::class)
@WebMvcTest(CourseController::class)
@ContextConfiguration(classes = [TestSecurityConfig::class])
class DraxoPeerGradingControllerTest {

    @Ignore
    @Test
    fun `test get DRAXO feedback as a learner`() {
        TODO("Implement this test")
    }
}