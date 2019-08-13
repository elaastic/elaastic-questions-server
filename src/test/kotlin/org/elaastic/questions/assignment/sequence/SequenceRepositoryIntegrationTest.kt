package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManager



@SpringBootTest
@Transactional
internal class SequenceRepositoryIntegrationTest(
        @Autowired val sequenceRepository: SequenceRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid sequence`() {
        val owner = testingService.getAnyUser()
        val assignment = testingService.getAnyAssignment()
        val statement = testingService.getAnyStatement()
        Sequence(
                rank = 1,
                owner = owner,
                assignment = assignment,
                statement = statement,
                executionContext = ExecutionContext.Blended
        ).tWhen {
            sequenceRepository.saveAndFlush(it)
            entityManager.refresh(it)
            it
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(it.version, equalTo(0L))
            assertThat(it.dateCreated, notNullValue())
            assertThat(it.lastUpdated, notNullValue())
            assertThat(it.rank, equalTo(1))
            assertThat(it.owner, equalTo(owner))
            assertThat(it.assignment, equalTo(assignment))
            assertThat(it.statement, equalTo(statement))
            assertThat(it.state, equalTo(State.beforeStart))
        }
    }
}
