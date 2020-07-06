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

package org.elaastic.questions.subject


import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.AssignmentRepository
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.subject.statement.StatementRepository
import org.elaastic.questions.test.TestingService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.junit.platform.commons.util.Preconditions.notNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import java.util.*
import javax.transaction.Transactional


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class SubjectRepositoryIntegrationTest (
        @Autowired val subjectRepository: SubjectRepository,
        @Autowired val testingService: TestingService,
        @Autowired val assignmentRepository: AssignmentRepository,
        @Autowired val statementRepository: StatementRepository
) {

    @Test
    fun `save a valid subject`() {
        // Given : a subject
        Subject(
                "My test subject",
                "My test course",
                testingService.getAnyUser(),
                UUID.randomUUID().toString()
        ).let {
            // When having it
            subjectRepository.saveAndFlush(it).let {
                // Then
                assertThat(it.id, not(nullValue()))
                assertThat(it.dateCreated, not(nullValue()))
                assertThat(it.lastUpdated, not(nullValue()))
                assertThat(it.version, equalTo(0L))
            }
        }
    }

    @Test
    fun `finding a subject with statements and assignments`(){
        // Given a subject with statements and assignments
        var randomUser = testingService.getAnyUser()
        var subject: Subject = Subject(
                "My test subject",
                "My test course",
                randomUser,
                UUID.randomUUID().toString()
        )

        subjectRepository.saveAndFlush(subject)

        Statement(
                randomUser,
                "My test statement",
                "Test content",
                QuestionType.OpenEnded,
                subject=subject
        ).let {
            statementRepository.saveAndFlush(it)}

        subjectRepository.findOneWithStatementsAndAssignmentsById(subject.id!!).let{
            assertThat(it!!, equalToObject(subject))
        }

    }
}
