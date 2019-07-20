package org.elaastic.questions.assignment

import org.elaastic.questions.test.TestingService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*
import javax.transaction.Transactional

/**
 * @author John Tranier
 */
@SpringBootTest
@Transactional
@EnableJpaAuditing
internal class AssignmentRepositoryIntegrationTest(
        @Autowired val assignmentRepository: AssignmentRepository,
        @Autowired val testingService: TestingService
) {

    @Test
    fun `save a valid assignmnt`() {
        // Given : an assignment
        Assignment(
                "My test assignment",
                testingService.getAnyUser(),
                UUID.randomUUID().toString()
        ).let {
            // When saving it
            assignmentRepository.saveAndFlush(it).let {
                // Then
                assertThat(it.id, not(nullValue()))
                assertThat(it.dateCreated, not(nullValue()))
                assertThat(it.lastUpdated, not(nullValue()))
                assertThat(it.version, equalTo(0L))
            }
        }
    }
}