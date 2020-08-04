package org.elaastic.questions.subject.statement

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.directory.User
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.AccessDeniedException
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class StatementServiceIntegrationTest(
        @Autowired val statementService: StatementService,
        @Autowired val statementRepository: StatementRepository,
        @Autowired val testingService: TestingService
) {

    @Test
    fun `get a statement - valid`() {
        val subject = testingService.getAnyTestSubject()

        MatcherAssert.assertThat(
                "The testing data are corrupted",
                subject.statements.size,
                CoreMatchers.equalTo(5)
        )

        val testingstatement = subject.statements.first()

        MatcherAssert.assertThat(
            statementService.get(testingstatement.owner,testingstatement.id!!),
            CoreMatchers.equalTo(testingstatement)
        )
    }

    @Test
    fun `get a statement - error unauthorized`() {
        val teacher: User = testingService.getTestTeacher()

        val subject = testingService.getAnyTestSubject()

        MatcherAssert.assertThat(
                "The testing data are corrupted",
                subject.statements.size,
                CoreMatchers.equalTo(5)
        )

        val testingstatement = subject.statements.first()
        assertThrows<AccessDeniedException> {
            statementService.get(teacher, testingstatement.id!!)
        }
    }

    @Test
    fun `get a statement - error invalid id`() {
        val user = testingService.getTestTeacher()

        assertThrows<EntityNotFoundException> {
            statementService.get(user, 12345678)
        }
    }

    @Test
    fun `duplicate statement when update`() {
        val subject = testingService.getAnyTestSubject()

        MatcherAssert.assertThat(
                "The testing data are corrupted",
                subject.statements.size,
                CoreMatchers.equalTo(5)
        )

        val testingStatement = subject.statements.first()
        val initialCount = statementRepository.countAllBySubject(testingStatement.subject!!)

        tWhen {
            statementService.duplicate(testingStatement)
        }.tThen {
            MatcherAssert.assertThat(
                    statementRepository.countAllBySubject(testingStatement.subject!!),
                    CoreMatchers.equalTo(initialCount + 1)
            )
        }

    }

    @Test
    fun `assign statement after update`() {
        val subject = testingService.getAnyTestSubject()

        MatcherAssert.assertThat(
                "The testing data are corrupted",
                subject.statements.size,
                CoreMatchers.equalTo(5)
        )

        val testingStatement = subject.statements.first()
        val duplicatedStatement = statementService.duplicate(testingStatement)
        duplicatedStatement.title = "Panda"
        var statementIdsBefore: MutableList<Long> = ArrayList()
        var statementIdsAfter: MutableList<Long> = ArrayList()

        for (assignment in subject.assignments) {
            for (sequence in assignment.sequences){
                statementIdsBefore.add(sequence.statement.id!!)
            }
        }

        tWhen {
            statementService.assignStatementToSequences(duplicatedStatement)
            for (assignment in subject.assignments) {
                for (sequence in assignment.sequences){
                    statementIdsAfter.add(sequence.statement.id!!)
                }
            }
        }.tThen {
            for (id in statementIdsBefore){
                if (duplicatedStatement.parentStatement!!.id == id)
                    MatcherAssert.assertThat("double replaced old", duplicatedStatement.id != id)
            }
        }

    }
}
