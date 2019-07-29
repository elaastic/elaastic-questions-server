package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tExpect
import org.springframework.beans.factory.annotation.Autowired
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceUnitUtil
import javax.transaction.Transactional


/**
 * @author John Tranier
 */
@SpringBootTest
@Transactional
@EnableJpaAuditing
internal class AssignmentServiceIntegrationTest(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {

    val persistentUnitUtil: PersistenceUnitUtil by lazy {
        entityManager.entityManagerFactory.persistenceUnitUtil
    }

    @Test
    fun `findAllByOwner - no assignment`() {
        val teacher = testingService.getTestTeacher()

        assignmentService.findAllByOwner(teacher)
                .tExpect {
                    assertThat(it.totalElements, equalTo(0L)
                    )
                }
    }

    @Test
    fun `findAllByOwner - with assignments`() {
        val teacher = testingService.getTestTeacher()
        createTestingData(teacher)

        assignmentService.findAllByOwner(teacher)
                .tExpect {
                    assertThat(it.totalElements, equalTo(10L))
                    assertThat(it.totalPages, equalTo(1))
                }

        assignmentService.findAllByOwner(teacher, PageRequest.of(0, 5))
                .tExpect {
                    assertThat(it.totalPages, equalTo(2))
                }
    }

    @Test
    fun `get an existing assignment without fetching sequences`() {
        val teacher = testingService.getTestTeacher()
        val assignmentId = assignmentService.save(
                Assignment(title = "Foo", owner = teacher)
        ).id!!

        entityManager.clear()

        assignmentService.get(assignmentId).let {
            assertThat(it.id, equalTo(assignmentId))
            assertThat(it.title, equalTo("Foo"))
            assertThat(
                    persistentUnitUtil.isLoaded(it, "sequences"),
                    equalTo(false)
            )
        }
    }

    @Test
    fun `get an existing assignment fetching sequences`() {
        val teacher = testingService.getTestTeacher()
        val assignmentId = assignmentService.save(
                Assignment(title = "Foo", owner = teacher)
        ).id!!

        entityManager.clear()

        assignmentService.get(assignmentId, true).let {
            assertThat(it.id, equalTo(assignmentId))
            assertThat(it.title, equalTo("Foo"))
            assertThat(
                    persistentUnitUtil.isLoaded(it, "sequences"),
                    equalTo(true)
            )
        }
    }

    @Test
    fun `try to get an assignment with an invalid id`() {
        assertThrows<EntityNotFoundException> {
            assignmentService.get(1234567L)
        }
    }

    private fun createTestingData(owner: User, n: Int = 10) {
        (1..n).forEach {
            assignmentService.save(
                    Assignment(
                            title = "Assignment n°$it",
                            owner = owner
                    )
            )
        }
    }
}