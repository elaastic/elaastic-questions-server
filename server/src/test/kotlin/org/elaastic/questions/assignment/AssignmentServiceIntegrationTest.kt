/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.assignment

import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionService
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.sequence.interaction.response.Response
import org.elaastic.sequence.interaction.response.ResponseRepository
import org.elaastic.sequence.interaction.response.ResponseService
import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.subject.statement.StatementRepository
import org.elaastic.questions.subject.statement.StatementService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import java.math.BigDecimal
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceUnitUtil
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
internal class AssignmentServiceIntegrationTest(
    @Autowired val assignmentService: AssignmentService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val entityManager: EntityManager,
    @Autowired val statementRepository: StatementRepository,
    @Autowired val subjectService: SubjectService,
    @Autowired val statementService: StatementService,
    @Autowired val responseService: ResponseService,
    @Autowired val interactionService: InteractionService,
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val sequenceService: SequenceService
) {

    val persistentUnitUtil: PersistenceUnitUtil by lazy {
        entityManager.entityManagerFactory.persistenceUnitUtil
    }

    @Test
    fun `findAllByOwner - no assignment`() {
        val teacher = integrationTestingService.getTestTeacher()

        assignmentService.findAllByOwner(teacher)
            .tExpect {
                assertThat(it.totalElements, equalTo(0L))
                assertThat(it.totalPages, equalTo(0))
            }
    }

    @Test
    fun `findAllByOwner - with assignments`() {
        val teacher = integrationTestingService.getTestTeacher()
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
        val teacher = integrationTestingService.getTestTeacher()
        val assignmentTitle = "An assignment"
        val assignmentId = assignmentService.save(
            Assignment(title = assignmentTitle, owner = teacher)
        ).id!!

        entityManager.clear()

        assignmentService.get(assignmentId).let {
            assertThat(it.id, equalTo(assignmentId))
            assertThat(it.title, equalTo(assignmentTitle))
            assertThat(
                persistentUnitUtil.isLoaded(it, "sequences"),
                equalTo(false)
            )
        }
    }

    @Test
    fun `get an existing assignment fetching sequences`() {
        val teacher = integrationTestingService.getTestTeacher()
        val assignmentTitle = "An assignment"
        val assignmentId = assignmentService.save(
            Assignment(title = assignmentTitle, owner = teacher)
        ).id!!

        entityManager.clear()

        assignmentService.get(assignmentId, true).let {
            assertThat(it.id, equalTo(assignmentId))
            assertThat(it.title, equalTo(assignmentTitle))
            assertThat(
                persistentUnitUtil.isLoaded(it, "sequences"),
                equalTo(true)
            )
        }
    }

    @Test
    fun `get an assignment for a user - OK`() {
        val teacher = integrationTestingService.getTestTeacher()
        val assignmentTitle = "An assignment"
        val assignmentId = assignmentService.save(
            Assignment(title = assignmentTitle, owner = teacher)
        ).id!!

        entityManager.clear()

        assertThat(assignmentService.get(teacher, assignmentId).id, equalTo(assignmentId))
    }

    @Test
    fun `try to get an assignment for a user that is owned by another user`() {
        val teacher = integrationTestingService.getTestTeacher()
        val assignmentTitle = "An assignment"
        val assignmentId = assignmentService.save(
            Assignment(title = assignmentTitle, owner = teacher)
        ).id!!

        entityManager.clear()

        assertThrows<AccessDeniedException> {
            assignmentService.get(integrationTestingService.getAnotherTestTeacher(), assignmentId)
        }
    }

    @Test
    fun `try to get an assignment with an invalid id`() {
        assertThrows<EntityNotFoundException> {
            assignmentService.get(1234567L)
        }
    }

    @Test
    fun `save a valid assignment`() {
        val assignment = Assignment("title", integrationTestingService.getTestTeacher())
        tWhen { assignmentService.save(assignment) }
            .tThen {
                assertThat(it.id, notNullValue())
                assertThat(it.version, equalTo(0L))
                assertThat(it.globalId, notNullValue())
                assertThat(it.sequences.size, equalTo(0))
                assertThat(it.owner, equalTo(integrationTestingService.getTestTeacher()))
            }
    }

    @Test
    fun `an assignment must have a not blank title`() {
        val exception = assertThrows<ConstraintViolationException> {
            assignmentService.save(Assignment("", integrationTestingService.getTestTeacher()))
        }

        assertThat(exception.constraintViolations.size, equalTo(1))
        assertThat(
            exception.constraintViolations.elementAt(0).propertyPath.toString(),
            equalTo("title")
        )
    }

    @Test
    fun `delete an assignment with results`() {
        val subject = integrationTestingService.getAnyTestSubject()
        val teacher = subject.owner
        val initialNbAssignment = assignmentService.count()
        val initialNbStatement = subjectService.countAllStatement(subject)
        val assignmentTitle = "An assignment"
        val assignment = subjectService.addAssignment(
            subject,
            Assignment(assignmentTitle, teacher)
        )
        val student = integrationTestingService.getTestStudent()

        sequenceService.start(
            teacher,
            assignment.sequences.first(),
            ExecutionContext.FaceToFace,
            false,
            0,
            null
        )

        val interaction =
            Interaction(
                interactionType = InteractionType.ResponseSubmission,
                rank = 1,
                owner = student,
                sequence = assignment.sequences.first()
            )
        interactionService.interactionRepository.saveAndFlush(interaction)
        interactionService.start(student, interaction.id!!)

        val initialNbResponse = responseService.count(assignment.sequences.first(), 1)

        val choiceListSpecification = LearnerChoice(listOf<Int>(1, 3))
        val response = Response(
            learner = student,
            interaction = interaction,
            attempt = 1,
            explanation = "explanation",
            confidenceDegree = ConfidenceDegree.CONFIDENT,
            meanGrade = BigDecimal(1),
            learnerChoice = choiceListSpecification,
            score = BigDecimal(2),
            statement = interaction.sequence.statement
        )
        responseRepository.save(response)

        tWhen {
            subjectService.removeAssignment(subject.owner, assignment)
        }.tThen {
            assertThat(assignmentService.count(), equalTo(initialNbAssignment))
            assertThat(subjectService.countAllStatement(subject), equalTo((initialNbStatement)))
            assertThat(responseService.count(assignment.sequences.first(), 1), equalTo(initialNbResponse))
        }
    }

    @Test
    fun `delete an assignment without results`() {
        val subject = integrationTestingService.getAnyTestSubject()
        val teacher = subject.owner
        val initialNbAssignment = assignmentService.count()
        val initialNbStatement = subjectService.countAllStatement(subject)
        val assignmentTitle = "An assignment"

        val assignment = subjectService.addAssignment(
            subject,
            Assignment(assignmentTitle, teacher)
        )

        entityManager.clear()

        tWhen {
            subjectService.removeAssignment(teacher, assignment)
        }.tThen {
            assertThat(assignmentService.count(), equalTo(initialNbAssignment))
            assertThat(subjectService.countAllStatement(subject), equalTo((initialNbStatement)))
        }
    }

    @Test
    fun `try to delete an assignment of another user`() {
        val teacher = integrationTestingService.getTestTeacher()
        val assignment = assignmentService.save(
            Assignment(title = "An assignment", owner = teacher)
        )

        assertThrows<IllegalArgumentException> {
            assignmentService.delete(integrationTestingService.getAnotherTestTeacher(), assignment)
        }
    }

    @Test
    fun `count sequences of empty assignment`() {
        val teacher = integrationTestingService.getTestTeacher()
        val assignment = assignmentService.save(
            Assignment(title = "An assignment", owner = teacher)
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
    fun `creating sequences for all statement upon assignment creation`() {
        val subject = integrationTestingService.getAnyTestSubject()
        val initialCount = subjectService.countAllStatement(subject)

        tWhen {
            subjectService.addAssignment(
                subject,
                Assignment(title = "An assignment", owner = subject.owner, subject = subject)
            )
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(
                assignmentService.countAllSequence(it),
                equalTo(initialCount)
            )
            assertThat(
                subjectService.countAllStatement(subject),
                equalTo(initialCount)
            ) // Should not add statement to subject
        }
    }

    @Test
    fun `add a sequence when a new statement is added to subject`() {
        val subject = integrationTestingService.getAnyTestSubject()
        val assignment = subjectService.addAssignment(
            subject,
            Assignment(title = "An assignment", owner = subject.owner, subject = subject)
        )
        val initialCount = assignmentService.countAllSequence(assignment)


        tWhen {
            subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(subject.owner)
                    .title("Sequence n°1")
                    .content("Content 1")
            )
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(
                assignmentService.countAllSequence(assignment),
                equalTo(initialCount + 1)
            )
            assertThat(
                subjectService.countAllStatement(subject),
                equalTo(initialCount + 1)
            )
        }
    }

    //TODO
    @Test
    fun `remove a sequence without results when a statement is removed from subject`() {
        val subject = integrationTestingService.getAnyTestSubject()
        val statement1 = subjectService.addStatement(
            subject,
            Statement.createDefaultStatement(subject.owner)
                .title("Sequence n°1")
                .content("Content 1")
        )
        val assignment = subjectService.addAssignment(
            subject,
            Assignment(title = "An assignment", owner = subject.owner, subject = subject)
        )
        val initialCount = assignmentService.countAllSequence(assignment)

        tWhen {
            subjectService.removeStatement(subject.owner, statement1)
        }.tThen {
            assertThat(
                "1er",
                subjectService.countAllStatement(subject),
                equalTo(initialCount - 1)
            )
            assertThat(
                "2eme",
                assignmentService.countAllSequence(assignment),
                equalTo(initialCount - 1)
            )

        }
    }

    @Test
    fun `not removing a sequence with result when a statement is removed from subject`() {
        val subject = integrationTestingService.getAnyTestSubject()
        val statement1 = statementService.get(618) // A statement linked to a sequence with results related
        val assignment = assignmentService.get(382)
        subject.addAssignment(assignment)

        val initialCountStatements = subjectService.countAllStatement(subject)
        val initialCount = assignmentService.countAllSequence(assignment)
        val stmtId = statement1.id!!

        tWhen {
            assertThat(
                " Before ",
                assignmentService.countAllSequence(assignment),
                equalTo(initialCount)
            )
            subjectService.removeStatement(subject.owner, statement1)
        }.tThen {
            assertThat(statementRepository.existsById(stmtId), equalTo(true))
            assertThat(
                " First ",
                assignmentService.countAllSequence(assignment),
                equalTo(initialCount)
            ) // The sequence must be kept
            assertThat(
                " Second ",
                subjectService.countAllStatement(subject),
                equalTo(initialCountStatements - 1)
            )
        }
    }


    @Test
    fun `findByGlobalId - not existing value`() {
        assertThat(
            assignmentService.findByGlobalId(UUID.randomUUID()),
            nullValue()
        )
    }

    @Test
    fun `findByGlobalId - existing value`() {
        val teacher = integrationTestingService.getTestTeacher()
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
        val teacher = integrationTestingService.getTestTeacher()
        val student = integrationTestingService.getTestStudent()

        val assignment = assignmentService.save(
            Assignment(
                title = "An assignment",
                owner = teacher
            )
        )

        tWhen {
            assignmentService.findAllAssignmentsForLearner(student)
        }.tExpect {
            assertThat(it.isEmpty(), equalTo(true))
        }

        tWhen {
            assignmentService.registerUser(student, assignment)
            assignmentService.findAllAssignmentsForLearner(student)
        }.tExpect {
            assertThat(it, equalTo(listOf(assignment)))
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
