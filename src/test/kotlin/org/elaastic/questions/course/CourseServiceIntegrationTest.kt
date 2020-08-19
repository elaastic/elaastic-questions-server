package org.elaastic.questions.course

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
import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.sequence.SequenceRepository
import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.subject.statement.StatementRepository
import org.elaastic.questions.subject.statement.StatementService
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
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
internal class CourseServiceIntegrationTest(
        @Autowired val courseService: CourseService,
        @Autowired val subjectService: SubjectService,
        @Autowired val entityManager: EntityManager,
        @Autowired val testingService: TestingService
) {
    val persistentUnitUtil: PersistenceUnitUtil by lazy {
        entityManager.entityManagerFactory.persistenceUnitUtil
    }

    @Test
    fun `findAllByOwner - no course`() {
        val teacher = testingService.getTestTeacher()

        courseService.findAllByOwner(teacher)
                .tExpect {
                    MatcherAssert.assertThat(it.totalElements, CoreMatchers.equalTo(0L))
                    MatcherAssert.assertThat(it.totalPages, CoreMatchers.equalTo(0))
                }
    }

}
