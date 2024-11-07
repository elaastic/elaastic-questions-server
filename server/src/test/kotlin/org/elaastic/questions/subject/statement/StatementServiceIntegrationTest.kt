package org.elaastic.questions.subject.statement

import org.elaastic.user.User
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.elaastic.questions.subject.SubjectService
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.security.access.AccessDeniedException
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
internal class StatementServiceIntegrationTest(
        @Autowired val statementService: StatementService,
        @Autowired val statementRepository: StatementRepository,
        @Autowired val subjectService: SubjectService,
        @Autowired val integrationTestingService: IntegrationTestingService
) {

    @Test
    fun `get a statement as the owner - valid`() {
        val subject = integrationTestingService.getAnyTestSubject()

        MatcherAssert.assertThat(
                "The testing data are corrupted",
                subject.statements.size,
                CoreMatchers.equalTo(2)
        )

        val testingstatement = subject.statements.first()

        MatcherAssert.assertThat(
            statementService.get(testingstatement.owner,testingstatement.id!!),
            CoreMatchers.equalTo(testingstatement)
        )
    }

    @Test
    fun `get a shared statement as a teacher but not the owner - valid`() {
        val subject = integrationTestingService.getAnyTestSubject()
        val teacher = integrationTestingService.getAnotherTestTeacher()
        MatcherAssert.assertThat(
                "The testing data are corrupted",
                subject.statements.size,
                CoreMatchers.equalTo(2)
        )

        val testingstatement = subject.statements.first()

        MatcherAssert.assertThat(
                statementService.get(teacher,testingstatement.id!!),
                CoreMatchers.equalTo(testingstatement)
        )
    }

    @Test
    fun `get a statement as a learner - error unauthorized`() {
        val learner: User = integrationTestingService.getTestStudent()

        val subject = integrationTestingService.getAnyTestSubject()

        MatcherAssert.assertThat(
                "The testing data are corrupted",
                subject.statements.size,
                CoreMatchers.equalTo(2)
        )

        val testingstatement = subject.statements.first()
        assertThrows<AccessDeniedException> {
            statementService.get(learner, testingstatement.id!!)
        }
    }

    @Test
    fun `get a statement - error invalid id`() {
        val user = integrationTestingService.getTestTeacher()

        assertThrows<EntityNotFoundException> {
            statementService.get(user, 12345678)
        }
    }

    @Test
    fun `duplicate statement when update`() {
        val subject = integrationTestingService.getAnyTestSubject()

        MatcherAssert.assertThat(
                "The testing data are corrupted",
                subject.statements.size,
                CoreMatchers.equalTo(2)
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
            MatcherAssert.assertThat(
                    it.owner,
                    CoreMatchers.equalTo(testingStatement.owner)
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
                    CoreMatchers.equalTo(testingStatement.subject)
            )
            MatcherAssert.assertThat(
                    it.rank,
                    CoreMatchers.equalTo(testingStatement.rank)
            )
            MatcherAssert.assertThat(
                    it.version,
                    CoreMatchers.equalTo(0L)
            )
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

    @Test
    fun `assign statement after update`() {
        val subject = integrationTestingService.getAnyTestSubject()

        MatcherAssert.assertThat(
                "The testing data are corrupted",
                subject.statements.size,
                CoreMatchers.equalTo(2)
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
