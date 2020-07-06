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

import com.nhaarman.mockitokotlin2.isNull
import org.elaastic.questions.assignment.sequence.FakeExplanationData
import org.elaastic.questions.assignment.sequence.SequenceRepository
import org.elaastic.questions.subject.statement.StatementService
import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.subject.statement.StatementRepository
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tGiven
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


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class SubjectServiceIntegrationTest(
        @Autowired val subjectService: SubjectService,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager,
        @Autowired val sequenceRepository: SequenceRepository,
        @Autowired val statementRepository: StatementRepository,
        @Autowired val statementService: StatementService
) {
    val persistentUnitUtil: PersistenceUnitUtil by lazy {
        entityManager.entityManagerFactory.persistenceUnitUtil
    }

    @Test
    fun `findAllByOwner - no subject`() {
        val teacher = testingService.getTestTeacher()

        subjectService.findAllByOwner(teacher)
                .tExpect {
                    assertThat(it.totalElements, equalTo(0L))
                    assertThat(it.totalPages, equalTo(0))
                }
    }

    @Test
    fun `findAllByOwner - with subjects`() {
        val teacher = testingService.getTestTeacher()
        createTestingData(teacher)

        subjectService.findAllByOwner(teacher)
                .tExpect {
                    assertThat(it.totalElements, equalTo(10L))
                    assertThat(it.totalPages, equalTo(1))
                }

        subjectService.findAllByOwner(teacher, PageRequest.of(0, 5))
                .tExpect {
                    assertThat(it.totalPages, equalTo(2))
                }
    }

    @Test
    fun `get an existing subject without fetching statements`() {
        val teacher = testingService.getTestTeacher()
        val subjectId = subjectService.save(
                Subject(title = "Foo", course ="New one" , owner = teacher)
        ).id!!

        entityManager.clear()

        subjectService.get(subjectId).let {
            assertThat(it.id, equalTo(subjectId))
            assertThat(it.title, equalTo("Foo"))
            assertThat(it.course, equalTo("New one"))
            assertThat(
                    persistentUnitUtil.isLoaded(it, "statements"),
                    equalTo(false)
            )
        }
    }

    @Test
    fun `get an existing assignment fetching sequences`() {
        val teacher = testingService.getTestTeacher()
        val subjectId = subjectService.save(
                Subject(title = "Foo", course ="New one" , owner = teacher)
        ).id!!

        entityManager.clear()

        subjectService.get(subjectId, true).let {
            assertThat(it.id, equalTo(subjectId))
            assertThat(it.title, equalTo("Foo"))
            assertThat(it.course, equalTo("New one"))
            assertThat(
                    persistentUnitUtil.isLoaded(it, "statements"),
                    equalTo(true)
            )
        }
    }

    @Test
    fun `get a subject for a user - OK`() {
        val teacher = testingService.getTestTeacher()
        val subjectId = subjectService.save(
                Subject(title = "Foo", course ="New one" , owner = teacher)
        ).id!!

        entityManager.clear()

        subjectService.get(teacher, subjectId).let {
            assertThat(it.id, equalTo(subjectId))
        }
    }

    @Test
    fun `try to get a subject for a user that is owned by another user`() {
        val teacher = testingService.getTestTeacher()
        val subjectId = subjectService.save(
                Subject(title = "Foo", course ="New one" , owner = teacher)
        ).id!!

        entityManager.clear()

        assertThrows<AccessDeniedException> {
            subjectService.get(testingService.getAnotherTestTeacher(), subjectId)
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
        val subject = Subject("subject", "course", testingService.getTestTeacher())
        tWhen { subjectService.save(subject) }
                .tThen {
                    assertThat(it.id, notNullValue())
                    assertThat(it.version, equalTo(0L))
                    assertThat(UUID.fromString(it.globalId), notNullValue())
                    assertThat(it.statements, equalTo(ArrayList()))
                    assertThat(it.assignments, equalTo(mutableSetOf()))
                    assertThat(it.owner, equalTo(testingService.getTestTeacher()))
                }
    }

    @Test
    fun `a subject must have a not blank title`() {
        val exception = assertThrows<ConstraintViolationException> {
            subjectService.save(Subject("", "course", testingService.getTestTeacher()))
        }

        assertThat(exception.constraintViolations.size, equalTo(1))
        assertThat(
                exception.constraintViolations.elementAt(0).propertyPath.toString(),
                equalTo("title")
        )
    }

    @Test
    fun `delete an existing subject`() {
        val teacher = testingService.getTestTeacher()
        val initialNbSubject = subjectService.count()
        val subject = subjectService.save(
                Subject("subject", "course", owner = teacher))

        entityManager.clear()

        tWhen {
            subjectService.delete(teacher, subject)
        }.tThen {
            assertThat(subjectService.count(), equalTo(initialNbSubject))
        }
    }

    @Test
    fun `try to delete a subject of another user`() {
        val subject = subjectService.save(
                Subject("subject", "course", testingService.getTestTeacher()))

        assertThrows<IllegalArgumentException> {
            subjectService.delete(testingService.getAnotherTestTeacher(), subject)
        }
    }

    @Test
    fun `count statements of empty subject`() {
        val subject = subjectService.save(
                Subject("subject", "course", testingService.getTestTeacher()))

        assertThat(
                subjectService.countAllStatement(subject),
                equalTo(0)
        )
    }

    @Test
    fun `count statements of the provided test subject`() {
        val teacher =  testingService.getTestTeacher()
        val subject = subjectService.save(
                Subject("subject", "course",teacher))
        subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Test")
                        .content("Test content")
        )
        subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Test2")
                        .content("Test content")
        )

        assertThat(
                subjectService.countAllStatement(
                        subjectService.get(subject.id!!)
                ),
                equalTo(2)
        )
    }

    @Test
    fun `add a statement to a subject - valid`() {
        val teacher = testingService.getTestTeacher()
        val subject = subjectService.save(
                Subject(title = "Foo", course ="New one" , owner = teacher)
        )

        tWhen {
            subjectService.addStatement(
                    subject,
                    Statement.createDefaultStatement(teacher)
                            .title("Test")
                            .content("Test content")
            )
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(it.subject, notNullValue())
            assertThat(
                    subjectService.countAllStatement(subject),
                    equalTo(1)
            )
        }
    }

    @Test
    fun `remove a statement to a subject - valid`() {
        val teacher = testingService.getTestTeacher()
        val subject = subjectService.save(
                Subject(title = "Foo", course ="New one" , owner = teacher)
        )
        val statement1 = subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Sequence n°1")
                        .content("Content 1")
        )
        val statement2 = subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Sequence n°2")
                        .content("Content 2")
        )
        val statement3 = subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Sequence n°3")
                        .content("Content 3")
        )
        entityManager.flush()

        val subjectId = subject.id
        val statementId1 = statement1.id
        val statementId2 = statement2.id
        val statementId3 = statement3.id
        val subjectLastUpdated = subject.lastUpdated
        val subjectVersion = subject.version

        assertThat(subjectId, notNullValue())
        assertThat(statementId1, notNullValue())
        assertThat(statementId2, notNullValue())
        assertThat(statementId3, notNullValue())

        tWhen {
            subjectService.removeStatement(statement2.owner, statement2)
            entityManager.flush()
            entityManager.clear()
        }.tThen {
            subjectService.get(subjectId!!, true).let {
                assertThat(it.statements.size, equalTo(2))
                assertThat(it.version, equalTo(subjectVersion!! + 1L))
            }
            assertThat(statementRepository.existsById(statementId2!!), equalTo(false)) // statement must be deleted
        }.tWhen {
            statementRepository.getOne(statementId1!!).let { statement ->
                subjectService.removeStatement(statement.owner, statement)
            }
            statementRepository.getOne(statementId3!!).let { statement ->
                subjectService.removeStatement(statement.owner, statement)
            }
            entityManager.flush()
        }.tThen {
            subjectService.get(subjectId!!, true).let {
                assertThat(it.statements.size, equalTo(0))
            }
        }
    }

    @Test
    fun `moveUp statement - the 1st statement`() {
        val teacher = testingService.getTestTeacher()
        val subject = subjectService.save(
                Subject(title = "Foo", course="Osef", owner = teacher)
        )
        val statement1 = subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Statement n°1")
                        .content("Content 1")
        )
        val statement2 = subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Statement n°2")
                        .content("Content 2")
        )

        tWhen {
            subjectService.moveUpStatement(subject, statement1.id!!)
            entityManager.clear()
        }.tExpect {
            subjectService.get(subject.id!!, true).let {
                assertThat(it.statements.size, equalTo(2))
                assertThat(
                        it.statements[0].title,
                        equalTo("Statement n°1")
                )
                assertThat(
                        it.statements[0].rank,
                        equalTo(1)
                )
                assertThat(
                        it.statements[1].title,
                        equalTo("Statement n°2")
                )
                assertThat(
                        it.statements[1].rank,
                        equalTo(2)
                )
            }
        }

    }

    @Test
    fun `moveUp statement - any statement but not the 1st`() {
        val teacher = testingService.getTestTeacher()
        val subject = subjectService.save(
                Subject(title = "Foo", course="Osef", owner = teacher)
        )
        val statement1 = subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Statement n°1")
                        .content("Content 1")
        )
        val statement2 = subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Statement n°2")
                        .content("Content 2")
        )

        tWhen {
            subjectService.moveUpStatement(subject, statement2.id!!)
            entityManager.clear()
        }.tExpect {
            subjectService.get(subject.id!!, true).let {
                assertThat(it.statements.size, equalTo(2))
                assertThat(
                        it.statements[0].title,
                        equalTo("Statement n°2")
                )
                assertThat(
                        it.statements[0].rank,
                        equalTo(1)
                )
                assertThat(
                        it.statements[1].title,
                        equalTo("Statement n°1")
                )
                assertThat(
                        it.statements[1].rank,
                        equalTo(2)
                )
            }
        }

    }

    @Test
    fun `moveDown statement - the last statement`() {
        val teacher = testingService.getTestTeacher()
        val subject = subjectService.save(
                Subject(title = "Foo", course="Osef", owner = teacher)
        )
        val statement1 = subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Statement n°1")
                        .content("Content 1")
        )
        val statement2 = subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Statement n°2")
                        .content("Content 2")
        )

        tWhen {
            subjectService.moveDownStatement(subject, statement2.id!!)
            entityManager.clear()
        }.tExpect {
            subjectService.get(subject.id!!, true).let {
                assertThat(it.statements.size, equalTo(2))
                assertThat(
                        it.statements[0].title,
                        equalTo("Statement n°1")
                )
                assertThat(
                        it.statements[0].rank,
                        equalTo(1)
                )
                assertThat(
                        it.statements[1].title,
                        equalTo("Statement n°2")
                )
                assertThat(
                        it.statements[1].rank,
                        equalTo(2)
                )
            }
        }

    }

    @Test
    fun `moveDown statement - any statement but not the last`() {
        val teacher = testingService.getTestTeacher()
        val subject = subjectService.save(
                Subject(title = "Foo", course="Osef", owner = teacher)
        )
        val statement1 = subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Statement n°1")
                        .content("Content 1")
        )
        val statement2 = subjectService.addStatement(
                subject,
                Statement.createDefaultStatement(teacher)
                        .title("Statement n°2")
                        .content("Content 2")
        )

        tWhen {
            subjectService.moveDownStatement(subject, statement1.id!!)
            entityManager.clear()
        }.tExpect {
            subjectService.get(subject.id!!, true).let {
                assertThat(it.statements.size, equalTo(2))
                assertThat(
                        it.statements[0].title,
                        equalTo("Statement n°2")
                )
                assertThat(
                        it.statements[0].rank,
                        equalTo(1)
                )
                assertThat(
                        it.statements[1].title,
                        equalTo("Statement n°1")
                )
                assertThat(
                        it.statements[1].rank,
                        equalTo(2)
                )
            }
        }

    }


    private fun createTestingData(owner: User, n: Int = 10) {
        (1..n).forEach {
            subjectService.save(
                    Subject(
                            title = "Sujet n°$it",
                            course = "no care",
                            owner = owner
                    )
            )
        }
    }
}
