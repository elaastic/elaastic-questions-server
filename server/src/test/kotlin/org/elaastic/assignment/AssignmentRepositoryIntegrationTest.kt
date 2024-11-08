package org.elaastic.assignment

import org.elaastic.questions.test.IntegrationTestingService
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class AssignmentRepositoryIntegrationTest(
    @Autowired val assignmentRepository: AssignmentRepository,
    @Autowired val integrationTestingService: IntegrationTestingService
) {

    @Test
    fun `save a valid assignment`() {
        // Given : an assignment
        Assignment(
            "My test assignment",
            integrationTestingService.getAnyUser(),
            UUID.randomUUID()
        ).let {
            // When saving it
            assignmentRepository.saveAndFlush(it).let {
                // Then
                MatcherAssert.assertThat(it.id, CoreMatchers.not(CoreMatchers.nullValue()))
                MatcherAssert.assertThat(it.dateCreated, CoreMatchers.not(CoreMatchers.nullValue()))
                MatcherAssert.assertThat(it.lastUpdated, CoreMatchers.not(CoreMatchers.nullValue()))
                MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(0L))
            }
        }
    }
}