package org.elaastic.material.instructional.subject

import org.elaastic.assignment.AssignmentRepository
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.material.instructional.statement.StatementRepository
import org.elaastic.questions.test.IntegrationTestingService
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class SubjectRepositoryIntegrationTest(
    @Autowired val subjectRepository: SubjectRepository,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val assignmentRepository: AssignmentRepository,
    @Autowired val statementRepository: StatementRepository
) {

    @Test
    fun `save a valid subject`() {
        // Given : a subject
        Subject(
            "My test subject",
            integrationTestingService.getAnyUser()
        ).let {
            // When having it
            subjectRepository.saveAndFlush(it).let {
                // Then
                MatcherAssert.assertThat(it.id, CoreMatchers.not(CoreMatchers.nullValue()))
                MatcherAssert.assertThat(it.dateCreated, CoreMatchers.not(CoreMatchers.nullValue()))
                MatcherAssert.assertThat(it.lastUpdated, CoreMatchers.not(CoreMatchers.nullValue()))
                MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(0L))
            }
        }
    }

    @Test
    fun `finding a subject with statements and assignments`() {
        // Given a subject with statements and assignments
        var randomUser = integrationTestingService.getAnyUser()
        var subject: Subject = Subject(
            "My test subject",
            randomUser
        )

        subjectRepository.saveAndFlush(subject)

        Statement(
            randomUser,
            "My test statement",
            "Test content",
            QuestionType.OpenEnded,
            subject = subject
        ).let {
            statementRepository.saveAndFlush(it)
        }

        subjectRepository.findOneWithStatementsAndAssignmentsById(subject.id!!).let {
            MatcherAssert.assertThat(it!!, CoreMatchers.equalToObject(subject))
        }

    }
}