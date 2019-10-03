package org.elaastic.questions.assignment

import org.elaastic.questions.assignment.sequence.SequenceRepository
import org.elaastic.questions.directory.User
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.springframework.beans.factory.annotation.Autowired
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceUnitUtil
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException
import kotlin.collections.ArrayList


@SpringBootTest
@Transactional
internal class AssignmentServiceIntegrationTest(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager,
        @Autowired val sequenceRepository: SequenceRepository,
        @Autowired val statementRepository: StatementRepository
) {

    val persistentUnitUtil: PersistenceUnitUtil by lazy {
        entityManager.entityManagerFactory.persistenceUnitUtil
    }

    @Test
    fun `findAllByOwner - no assignment`() {
        val teacher = testingService.getTestTeacher()

        assignmentService.findAllByOwner(teacher)
                .tExpect {
                    assertThat(it.totalElements, equalTo(0L))
                    assertThat(it.totalPages, equalTo(0))
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
    fun `get an assignment for a user - OK`() {
        val teacher = testingService.getTestTeacher()
        val assignmentId = assignmentService.save(
                Assignment(title = "Foo", owner = teacher)
        ).id!!

        entityManager.clear()

        assignmentService.get(teacher, assignmentId).let {
            assertThat(it.id, equalTo(assignmentId))
        }
    }

    @Test
    fun `try to get an assignement for a user that is owned by another user`() {
        val teacher = testingService.getTestTeacher()
        val assignmentId = assignmentService.save(
                Assignment(title = "Foo", owner = teacher)
        ).id!!

        entityManager.clear()

        assertThrows<AccessDeniedException> {
            assignmentService.get(testingService.getAnotherTestTeacher(), assignmentId)
        }
    }

    @Test
    fun `try to get an assignment with an invalid id`() {
        assertThrows<EntityNotFoundException> {
            assignmentService.get(1234567L)
        }
    }

    @Test
    fun `save a valid assignent`() {
        val assignment = Assignment("title", testingService.getTestTeacher())
        tWhen { assignmentService.save(assignment) }
                .tThen {
                    assertThat(it.id, notNullValue())
                    assertThat(it.version, equalTo(0L))
                    assertThat(UUID.fromString(it.globalId), notNullValue())
                    assertThat(it.sequences, equalTo(ArrayList()))
                    assertThat(it.owner, equalTo(testingService.getTestTeacher()))
                }
    }

    @Test
    fun `an assignment must have a not blank title`() {
        val exception = assertThrows<ConstraintViolationException> {
            assignmentService.save(Assignment("", testingService.getTestTeacher()))
        }

        assertThat(exception.constraintViolations.size, equalTo(1))
        assertThat(
                exception.constraintViolations.elementAt(0).propertyPath.toString(),
                equalTo("title")
        )
    }

    @Test
    fun `delete an existing assignment`() {
        val teacher = testingService.getTestTeacher()
        val initialNbAssignment = assignmentService.count()
        val assignment = assignmentService.save(
                Assignment(title = "Foo", owner = teacher)
        )

        entityManager.clear()

        tWhen {
            assignmentService.delete(teacher, assignment)
        }.tThen {
            assertThat(assignmentService.count(), equalTo(initialNbAssignment))
        }
    }

    @Test
    fun `try to delete an assignment of another user`() {
        val teacher = testingService.getTestTeacher()
        val assignment = assignmentService.save(
                Assignment(title = "Foo", owner = teacher)
        )

        assertThrows<IllegalArgumentException> {
            assignmentService.delete(testingService.getAnotherTestTeacher(), assignment)
        }
    }

    @Test
    fun `count sequences of empty assignment`() {
        val teacher = testingService.getTestTeacher()
        val assignment = assignmentService.save(
                Assignment(title = "Foo", owner = teacher)
        )

        assertThat(
                assignmentService.countAllSequence(assignment),
                equalTo(0)
        )
    }

    @Test
    fun `count sequences of the provided test assignment`() {
        assertThat(
                assignmentService.countAllSequence(
                        assignmentService.get(382)
                ),
                equalTo(2)
        )
    }

    @Test
    fun `add a sequence to an assignment - valid`() {
        val teacher = testingService.getTestTeacher()
        val assignment = assignmentService.save(
                Assignment(title = "Foo", owner = teacher)
        )

        tWhen {
            assignmentService.addSequence(
                    assignment,
                    Statement.createDefaultStatement(teacher)
                            .title("Test")
                            .content("Test content")
            )
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(it.statement.id, notNullValue())
            assertThat(
                    assignmentService.countAllSequence(assignment),
                    equalTo(1)
            )
        }
    }

    @Test
    fun `remove a sequence to an assignment - valid`() {
        val teacher = testingService.getTestTeacher()
        val assignment = assignmentService.save(
                Assignment(title = "Foo", owner = teacher)
        )
        val sequence1 = assignmentService.addSequence(
                assignment,
                Statement.createDefaultStatement(teacher)
                        .title("Sequence n°1")
                        .content("Content 1")
        )
        val sequence2 = assignmentService.addSequence(
                assignment,
                Statement.createDefaultStatement(teacher)
                        .title("Sequence n°2")
                        .content("Content 2")
        )
        val sequence3 = assignmentService.addSequence(
                assignment,
                Statement.createDefaultStatement(teacher)
                        .title("Sequence n°3")
                        .content("Content 3")
        )
        entityManager.flush()

        val assignmentId = assignment.id
        val sequenceId1 = sequence1.id
        val statementId1 = sequence1.statement.id
        val sequenceId2 = sequence2.id
        val statementId2 = sequence2.statement.id
        val sequenceId3 = sequence3.id
        val statementId3 = sequence3.statement.id
        val assignmentLastUpdated = assignment.lastUpdated
        val assignmentVersion = assignment.version

        assertThat(assignmentId, notNullValue())
        assertThat(sequenceId1, notNullValue())
        assertThat(statementId1, notNullValue())
        assertThat(sequenceId2, notNullValue())
        assertThat(statementId2, notNullValue())
        assertThat(sequenceId3, notNullValue())
        assertThat(statementId3, notNullValue())

        tWhen {
            assignmentService.removeSequence(sequence2)
            entityManager.flush()
            entityManager.clear()
        }.tThen {
            assignmentService.get(assignmentId!!, true).let {
                assertThat(it.sequences.size, equalTo(2))
                assertThat(it.version, equalTo(assignmentVersion!! + 1L))
            }
            assertThat(sequenceRepository.existsById(sequenceId2!!), equalTo(false))
            assertThat(statementRepository.existsById(statementId2!!), equalTo(false))
        }.tWhen {
            assignmentService.removeSequence(sequenceRepository.getOne(sequenceId1!!))
            assignmentService.removeSequence(sequenceRepository.getOne(sequenceId3!!))
            entityManager.flush()
        }.tThen {
            assignmentService.get(assignmentId!!, true).let {
                assertThat(it.sequences.size, equalTo(0))
            }
        }
    }

    @Test
    fun `moveUp sequence - the 1st sequence`() {
        val teacher = testingService.getTestTeacher()
        val assignment = assignmentService.save(
                Assignment(title = "Foo", owner = teacher)
        )
        val sequence1 = assignmentService.addSequence(
                assignment,
                Statement.createDefaultStatement(teacher)
                        .title("Sequence n°1")
                        .content("Content 1")
        )
        val sequence2 = assignmentService.addSequence(
                assignment,
                Statement.createDefaultStatement(teacher)
                        .title("Sequence n°2")
                        .content("Content 2")
        )

        tWhen {
            assignmentService.moveUpSequence(assignment, sequence1.id!!)
            entityManager.clear()
        }.tExpect {
            assignmentService.get(assignment.id!!, true).let {
                assertThat(it.sequences.size, equalTo(2))
                assertThat(
                        it.sequences[0].statement.title,
                        equalTo("Sequence n°1")
                )
                assertThat(
                        it.sequences[0].rank,
                        equalTo(1)
                )
                assertThat(
                        it.sequences[1].statement.title,
                        equalTo("Sequence n°2")
                )
                assertThat(
                        it.sequences[1].rank,
                        equalTo(2)
                )
            }
        }

    }

    @Test
    fun `moveUp sequence - any sequence but not the 1st`() {
        val teacher = testingService.getTestTeacher()
        val assignment = assignmentService.save(
                Assignment(title = "Foo", owner = teacher)
        )
        val sequence1 = assignmentService.addSequence(
                assignment,
                Statement.createDefaultStatement(teacher)
                        .title("Sequence n°1")
                        .content("Content 1")
        )
        val sequence2 = assignmentService.addSequence(
                assignment,
                Statement.createDefaultStatement(teacher)
                        .title("Sequence n°2")
                        .content("Content 2")
        )

        tWhen {
            assignmentService.moveUpSequence(assignment, sequence2.id!!)
            entityManager.clear()
        }.tExpect {
            assignmentService.get(assignment.id!!, true).let {
                assertThat(it.sequences.size, equalTo(2))
                assertThat(
                        it.sequences[0].statement.title,
                        equalTo("Sequence n°2")
                )
                assertThat(
                        it.sequences[0].rank,
                        equalTo(1)
                )
                assertThat(
                        it.sequences[1].statement.title,
                        equalTo("Sequence n°1")
                )
                assertThat(
                        it.sequences[1].rank,
                        equalTo(2)
                )
            }
        }

    }

    @Test
    fun `findByGlobalId - not existing value`() {
        assertThat(
                assignmentService.findByGlobalId("not existing"),
                nullValue()
        )
    }

    @Test
    fun `findByGlobalId - existing value`() {
        val teacher = testingService.getTestTeacher()
        assignmentService.save(
                Assignment(
                        title = "An assignment",
                        owner = teacher
                )
        ).tExpect {
            assertThat(
                    assignmentService.findByGlobalId(it.globalId),
                    equalTo(it)
            )
        }
    }

    @Test
    fun `register a student to an assignment`() {
        val teacher = testingService.getTestTeacher()
        val student = testingService.getTestStudent()

        val assignment = assignmentService.save(
                Assignment(
                        title = "An assignment",
                        owner = teacher
                )
        )

        tWhen {
            assignmentService.findAllAssignmentsForLearner(student)
        }.tExpect {
            assertThat(it.isEmpty, equalTo(true))
        }

        tWhen {
            assignmentService.registerUser(student, assignment)
            assignmentService.findAllAssignmentsForLearner(student)
        }.tExpect {
            assertThat(it.content, equalTo(listOf(assignment)))
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