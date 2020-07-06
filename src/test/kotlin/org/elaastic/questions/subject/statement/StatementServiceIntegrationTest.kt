package org.elaastic.questions.subject.statement

import org.elaastic.questions.directory.User
import org.elaastic.questions.test.TestingService
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
        @Autowired val testingService: TestingService
) {

    @Test
    fun `get a statement - valid`() {
        val subject = testingService.getAnyTestSubject()

        MatcherAssert.assertThat(
                "The testing data are corrupted",
                subject.statements.size,
                CoreMatchers.equalTo(3)
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
                CoreMatchers.equalTo(3)
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
}
