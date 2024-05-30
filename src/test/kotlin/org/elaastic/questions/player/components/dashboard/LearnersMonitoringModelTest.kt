package org.elaastic.questions.player.components.dashboard

import org.elaastic.questions.test.IntegrationTestingService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class LearnersMonitoringModelTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
){
    @Test
    fun `test test`() {
        assertTrue(true)
    }
}