package org.elaastic.questions.subject

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

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.course.Course
import org.elaastic.questions.course.CourseService
import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.subject.statement.StatementRepository
import org.elaastic.questions.subject.statement.StatementService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceUnitUtil
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException
import kotlin.collections.ArrayList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
internal class SubjectServiceIntegrationTest(
    @Autowired val subjectService: SubjectService,
    @Autowired val courseService: CourseService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val entityManager: EntityManager,
    @Autowired val statementRepository: StatementRepository,
    @Autowired val statementService: StatementService,
    @Autowired val sharedSubjectRepository: SharedSubjectRepository
) {
    val persistentUnitUtil: PersistenceUnitUtil by lazy {
        entityManager.entityManagerFactory.persistenceUnitUtil
    }

    @Test
    fun `findAllByOwner - no subject`() {
        val teacher = integrationTestingService.getTestTeacher()

        subjectService.findAllByOwner(teacher)
            .tExpect {
                MatcherAssert.assertThat(it.totalElements, CoreMatchers.equalTo(0L))
                MatcherAssert.assertThat(it.totalPages, CoreMatchers.equalTo(0))
            }
    }

    @Test
    fun `findAllByOwner - with subjects`() {
        val teacher = integrationTestingService.getTestTeacher()
        createTestingData(teacher)

        subjectService.findAllByOwner(teacher)
            .tExpect {
                MatcherAssert.assertThat(it.totalElements, CoreMatchers.equalTo(10L))
                MatcherAssert.assertThat(it.totalPages, CoreMatchers.equalTo(1))
            }

        subjectService.findAllByOwner(teacher, PageRequest.of(0, 5))
            .tExpect {
                MatcherAssert.assertThat(it.totalPages, CoreMatchers.equalTo(2))
            }
    }

    @Test
    fun `get an existing subject without fetching statements and assignments`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subjectId = subjectService.save(
            Subject("Subject", teacher)
        ).id!!

        entityManager.clear()

        subjectService.get(subjectId).let {
            MatcherAssert.assertThat(it.id, CoreMatchers.equalTo(subjectId))
            MatcherAssert.assertThat(it.title, CoreMatchers.equalTo("Subject"))
            MatcherAssert.assertThat(
                persistentUnitUtil.isLoaded(it, "statements"),
                CoreMatchers.equalTo(false)
            )
            MatcherAssert.assertThat(
                persistentUnitUtil.isLoaded(it, "assignments"),
                CoreMatchers.equalTo(false)
            )
        }
    }

    @Test
    fun `get an existing subject fetching statements and assignments`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subjectId = subjectService.save(
            Subject("Subject", teacher)
        ).id!!

        entityManager.clear()

        subjectService.get(subjectId, true).let {
            MatcherAssert.assertThat(it.id, CoreMatchers.equalTo(subjectId))
            MatcherAssert.assertThat(it.title, CoreMatchers.equalTo("Subject"))
            MatcherAssert.assertThat(
                persistentUnitUtil.isLoaded(it, "statements"),
                CoreMatchers.equalTo(true)
            )
            MatcherAssert.assertThat(
                persistentUnitUtil.isLoaded(it, "assignments"),
                CoreMatchers.equalTo(true)
            )
        }
    }

    @Test
    fun `get a subject for a user - OK`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subjectId = subjectService.save(
            Subject("Subject", teacher)
        ).id!!

        entityManager.clear()

        subjectService.get(teacher, subjectId).let {
            MatcherAssert.assertThat(it.id, CoreMatchers.equalTo(subjectId))
            MatcherAssert.assertThat(it.owner, CoreMatchers.equalTo(teacher))
        }
    }

    @Test
    fun `try to get a subject for a user that is a teacher with a shared access but not the owner`() {
        val teacher = integrationTestingService.getTestTeacher()
        val teacher2 = integrationTestingService.getAnotherTestTeacher()
        val subjectId = subjectService.save(
            Subject("Subject", teacher)
        ).id!!
        subjectService.sharedToTeacher(
            teacher2,
            subjectService.get(teacher, subjectId)
        )

        entityManager.clear()

        subjectService.get(teacher2, subjectId).let {
            MatcherAssert.assertThat(it.id, CoreMatchers.equalTo(subjectId))
            MatcherAssert.assertThat(
                "Subject is shared to another teacher",
                sharedSubjectRepository.findByTeacherAndSubject(teacher2, it) != null
            )
            MatcherAssert.assertThat("Subject is accessed by another teacher", it.owner != teacher2)
        }
    }

    @Test
    fun `try to get a subject for a user that is a teacher without shared access`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subjectId = subjectService.save(
            Subject("Subject", teacher)
        ).id!!

        entityManager.clear()

        assertThrows<AccessDeniedException> {
            subjectService.get(integrationTestingService.getAnotherTestTeacher(), subjectId)
        }
    }

    @Test
    fun `try to get a subject for a user that is a learner`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subjectId = subjectService.save(
            Subject("Subject", teacher)
        ).id!!

        entityManager.clear()

        assertThrows<AccessDeniedException> {
            subjectService.get(integrationTestingService.getTestStudent(), subjectId)
        }
    }

    @Test
    fun `try to get a subject with an invalid id`() {
        assertThrows<EntityNotFoundException> {
            subjectService.get(1234567L)
        }
    }

    @Test
    fun `save a valid subject`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subject = subjectService.save(Subject("Subject", teacher))
        tWhen { subjectService.save(subject) }
            .tThen {
                MatcherAssert.assertThat(it.id, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(0L))
                MatcherAssert.assertThat(it.globalId, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.statements.size, CoreMatchers.equalTo(0))
                MatcherAssert.assertThat(it.assignments, CoreMatchers.equalTo(mutableSetOf()))
                MatcherAssert.assertThat(it.title, CoreMatchers.equalTo("Subject"))
                MatcherAssert.assertThat(it.owner, CoreMatchers.equalTo(integrationTestingService.getTestTeacher()))
            }
    }

    @Test
    fun `a subject must have a not blank title`() {
        val exception = assertThrows<ConstraintViolationException> {
            subjectService.save(Subject("", integrationTestingService.getTestTeacher()))
        }

        MatcherAssert.assertThat(exception.constraintViolations.size, CoreMatchers.equalTo(1))
        MatcherAssert.assertThat(
            exception.constraintViolations.elementAt(0).propertyPath.toString(),
            CoreMatchers.equalTo("title")
        )
    }

    @Test
    fun `delete an existing subject`() {
        val teacher = integrationTestingService.getTestTeacher()
        val initialNbSubject = subjectService.count()
        val subject = subjectService.save(
            Subject("Subject", teacher)
        )

        entityManager.clear()

        tWhen {
            subjectService.delete(teacher, subject)
        }.tThen {
            MatcherAssert.assertThat(subjectService.count(), CoreMatchers.equalTo(initialNbSubject))
        }
    }

    //

    @Test
    fun `try to delete a subject of another user`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subject = subjectService.save(
            Subject(title = "Foo", owner = teacher)
        )

        assertThrows<IllegalArgumentException> {
            subjectService.delete(integrationTestingService.getAnotherTestTeacher(), subject)
        }
    }

    @Test
    fun `count statements of empty subject`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subject = subjectService.save(
            Subject(title = "Foo", owner = teacher)
        )

        MatcherAssert.assertThat(
            subjectService.countAllStatement(subject),
            CoreMatchers.equalTo(0)
        )
    }

    @Test
    fun `count statements of the provided test subject`() {
        val subject = subjectService.get(1)

        MatcherAssert.assertThat(
            subjectService.countAllStatement(subject),
            CoreMatchers.equalTo(2)
        )
    }

    @Test
    fun `add a statement to a subject - valid`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subject = integrationTestingService.getAnyTestSubject()
        val initialCount = subjectService.countAllStatement(subject)

        tWhen {
            subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                    .title("Sequence n°1")
                    .content("Content 1")
            )
        }.tThen {
            MatcherAssert.assertThat(it.id, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(
                subjectService.countAllStatement(subject),
                CoreMatchers.equalTo(initialCount + 1)
            )
        }
    }

    @Test
    fun `remove unused statement from a subject - no duplicate`() {
        val subject = integrationTestingService.getAnyTestSubject()
        val initialCount = subjectService.countAllStatement(subject)

        val statement1 = subjectService.addStatement(
            subject,
            Statement.createDefaultStatement(subject.owner)
                .title("Statement n°1")
                .content("Content 1")
        )


        entityManager.flush()

        val subjectVersion = subject.version
        val statementId1 = statement1.id!!


        MatcherAssert.assertThat(statement1.id, CoreMatchers.notNullValue())

        tWhen {
            subjectService.removeStatement(statement1.owner, statement1)
            entityManager.flush()
            entityManager.clear()
        }.tThen {
            subjectService.get(subject.id!!, true).let {
                MatcherAssert.assertThat(
                    subjectService.countAllStatement(subject),
                    CoreMatchers.equalTo(initialCount)
                )
                MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(subjectVersion!! + 1L))
            } // Statement must be erased because it has no more use
            MatcherAssert.assertThat(statementRepository.existsById(statementId1), CoreMatchers.equalTo(false))
        }
    }

    @Test
    fun `remove used statement from a subject - keep the statement in database`() {
        val subject = integrationTestingService.getAnyTestSubject()
        val statement1 = statementService.get(618) // A statement linked to a sequence with results related
        subjectService.addAssignment(
            subject,
            Assignment(title = "Foo", owner = subject.owner, subject = subject)
        )

        val initialCount = subjectService.countAllStatement(subject)
        val stmtId = statement1.id!!

        tWhen {
            subjectService.removeStatement(statement1.owner, statement1)
        }.tThen {
            MatcherAssert.assertThat(statementRepository.existsById(stmtId), CoreMatchers.equalTo(true))
            subjectService.get(subject.id!!, true).let {
                MatcherAssert.assertThat(
                    subjectService.countAllStatement(subject),
                    CoreMatchers.equalTo(initialCount - 1)
                )
            }
        }
    }


    @Test
    fun `findByGlobalId - not existing value`() {
        MatcherAssert.assertThat(
            subjectService.findByGlobalId(UUID.randomUUID()),
            CoreMatchers.nullValue()
        )
    }

    @Test
    fun `findByGlobalId - existing value`() {
        val teacher = integrationTestingService.getTestTeacher()
        subjectService.save(
            Subject(
                title = "An assignment",
                owner = teacher
            )
        ).tExpect {
            MatcherAssert.assertThat(
                subjectService.findByGlobalId(it.globalId),
                CoreMatchers.equalTo(it)
            )
        }
    }

    @Test
    fun `duplicate a subject`() {
        // given a teacher
        val teacher = integrationTestingService.getTestTeacher()
        // a course
        val course = courseService.save(Course("A course", teacher))
        // and a subject associated with the course
        val subject = subjectService.save(
            Subject(
                title = "A subject",
                owner = teacher,
                course = course
            )
        )
        subjectService.addStatement(
            subject,
            Statement.createDefaultStatement(teacher)
                .title("Stmt n°1")
                .content("Content 1")
        )
        subjectService.addStatement(
            subject,
            Statement.createDefaultStatement(teacher)
                .title("Stmt n°2")
                .content("Content 2")
        )

        val nbSubjectTeacher = subjectService.findAllByOwner(teacher).totalElements

        subjectService.duplicate(teacher, subject)
            .tExpect {
                MatcherAssert.assertThat(
                    subjectService.findByGlobalId(it.globalId),
                    CoreMatchers.equalTo(it)
                )
                MatcherAssert.assertThat(
                    it.title,
                    CoreMatchers.equalTo(subject.title + " (2) ")
                )
                MatcherAssert.assertThat(
                    it.course,
                    CoreMatchers.equalTo(course)
                )
                MatcherAssert.assertThat(
                    it.parentSubject,
                    CoreMatchers.equalTo(subject)
                )
                MatcherAssert.assertThat(it.dateCreated, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.lastUpdated, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(
                    it.statements.size,
                    CoreMatchers.equalTo(subject.statements.size)
                )
                MatcherAssert.assertThat(
                    it.owner,
                    CoreMatchers.equalTo(teacher)
                )
                MatcherAssert.assertThat(
                    subjectService.findAllByOwner(teacher).totalElements,
                    CoreMatchers.equalTo(nbSubjectTeacher + 1)
                )
            }
    }

    @Test
    fun `import a subject - valid`() {
        val teacher = integrationTestingService.getTestTeacher()
        val otherTeacher = integrationTestingService.getAnotherTestTeacher()
        val subject = subjectService.save(
            Subject(
                title = "A subject",
                owner = teacher
            )
        )
        subjectService.addStatement(
            subject,
            Statement.createDefaultStatement(teacher)
                .title("Stmt n°1")
                .content("Content 1")
        )
        subjectService.addStatement(
            subject,
            Statement.createDefaultStatement(teacher)
                .title("Stmt n°2")
                .content("Content 2")
        )

        val nbSubjectTeacher = subjectService.findAllByOwner(teacher).totalElements
        val nbSubjectOtherTeacher = subjectService.findAllByOwner(otherTeacher).totalElements
        subjectService.sharedToTeacher(otherTeacher, subject)

        subjectService.import(otherTeacher, subject)
            .tExpect {
                MatcherAssert.assertThat(
                    subjectService.findByGlobalId(it.globalId),
                    CoreMatchers.equalTo(it)
                )
                MatcherAssert.assertThat(
                    it.title,
                    CoreMatchers.equalTo(subject.title)
                )
                MatcherAssert.assertThat(
                    it.parentSubject,
                    CoreMatchers.equalTo(subject)
                )
                MatcherAssert.assertThat(it.dateCreated, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.lastUpdated, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(
                    it.statements.size,
                    CoreMatchers.equalTo(subject.statements.size)
                )
                MatcherAssert.assertThat(
                    it.owner,
                    CoreMatchers.equalTo(otherTeacher)
                )
                MatcherAssert.assertThat(
                    subjectService.findAllByOwner(teacher).totalElements,
                    CoreMatchers.equalTo(nbSubjectTeacher)
                )
                MatcherAssert.assertThat(
                    subjectService.findAllByOwner(otherTeacher).totalElements,
                    CoreMatchers.equalTo(nbSubjectOtherTeacher + 1)
                )
            }
    }

    @Test
    fun `import a subject - not shared with you`() {
        val teacher = integrationTestingService.getTestTeacher()
        val otherTeacher = integrationTestingService.getAnotherTestTeacher()
        val subject = subjectService.save(
            Subject(
                title = "An assignment",
                owner = teacher
            )
        )

        assertThrows<EntityNotFoundException> {
            subjectService.import(otherTeacher, subject)
        }

    }

    private fun createTestingData(owner: User, n: Int = 10) {
        (1..n).forEach {
            subjectService.save(
                Subject(
                    title = "Subject n°$it",
                    owner = owner
                )
            )
        }
    }

    @Test
    fun `import a statement`() {
        val subject = integrationTestingService.getAnyTestSubject()

        MatcherAssert.assertThat(
            "The testing data are corrupted",
            subject.statements.size,
            CoreMatchers.equalTo(2)
        )
        val newSubject = Subject("Yolo", integrationTestingService.getAnotherTestTeacher())
        subjectService.save(newSubject)

        val testingStatement = subject.statements.first()
        val initialCount = statementRepository.countAllBySubject(testingStatement.subject!!)

        tWhen {
            subjectService.importStatementInSubject(testingStatement, newSubject)
        }.tThen {
            MatcherAssert.assertThat(
                statementRepository.countAllBySubject(newSubject),
                CoreMatchers.equalTo(1)
            ) // NewSubject has one more statement
            MatcherAssert.assertThat(
                statementRepository.countAllBySubject(testingStatement.subject!!),
                CoreMatchers.equalTo(initialCount)
            ) // OldSubject is unchanged
            MatcherAssert.assertThat(
                it.owner,
                CoreMatchers.equalTo(newSubject.owner)
            )
            MatcherAssert.assertThat(
                it.title,
                CoreMatchers.equalTo(testingStatement.title)
            )
            MatcherAssert.assertThat(
                it.content,
                CoreMatchers.equalTo(testingStatement.content)
            )
            MatcherAssert.assertThat(
                it.questionType,
                CoreMatchers.equalTo(testingStatement.questionType)
            )
            MatcherAssert.assertThat(
                it.questionType,
                CoreMatchers.equalTo(testingStatement.questionType)
            )
            MatcherAssert.assertThat(
                it.choiceSpecification,
                CoreMatchers.equalTo(testingStatement.choiceSpecification)
            )
            MatcherAssert.assertThat(
                it.parentStatement,
                CoreMatchers.equalTo(testingStatement)
            )
            MatcherAssert.assertThat(
                it.expectedExplanation,
                CoreMatchers.equalTo(testingStatement.expectedExplanation)
            )
            MatcherAssert.assertThat(
                it.subject,
                CoreMatchers.equalTo(newSubject)
            )
            MatcherAssert.assertThat(
                it.rank,
                CoreMatchers.equalTo(1)
            )
            MatcherAssert.assertThat(
                it.version,
                CoreMatchers.equalTo(1L)
            ) // Duplicate put version to 0, Import is made one step beyond duplicate, so version increased by 1
            MatcherAssert.assertThat(
                it.attachment,
                CoreMatchers.equalTo(testingStatement.attachment)
            )
            MatcherAssert.assertThat(
                statementService.findAllFakeExplanationsForStatement(it).size,
                CoreMatchers.equalTo(statementService.findAllFakeExplanationsForStatement(testingStatement).size)
            )
        }

    }
}


