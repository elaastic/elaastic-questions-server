package org.elaastic.questions.assignment

import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.sequence.TeacherExplanation
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.elaastic.questions.test.TestingService
import org.exparity.hamcrest.date.DateMatchers
import java.util.*
import javax.persistence.EntityManager
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Assertions



@SpringBootTest
@Transactional
internal class StatementRepositoryIntegrationTest(
        @Autowired val statementRepository: StatementRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save and resave a valid statement`() {
        // Given :
        val title = "title"
        val content = "content"
        val user = testingService.getAnyUser()

        val justBefore = Date()

        // When :
        val statement =
                statementRepository.saveAndFlush(
                        Statement(
                                owner = user,
                                title = title,
                                content = content,
                                questionType = QuestionType.OpenEnded
                        )
                )

        val justAfter = Date()

        // Then :
        assertThat(statement.id, notNullValue())
        assertThat(statement.version, equalTo(0L))
        assertThat(statement.dateCreated, DateMatchers.sameOrAfter(justBefore))
        assertThat(statement.dateCreated, DateMatchers.sameOrBefore(justAfter))


        // And when :
        statement.content = "updated"
        val statementV2 = statementRepository.saveAndFlush(
                statement
        )

        // Then :
        statementV2.let {
            assertThat(it.version, equalTo(1L))
            Assertions.assertTrue(it.dateCreated < it.lastUpdated)
        }
    }

    @Test
    fun `read legacy data`() {
        assertThat(
                statementRepository.findAll().count(),
                equalTo(5)
        )
    }

    @Test
    fun `create a statement with a parent statement`() {
        // Given:
        val user = testingService.getAnyUser()

        val parentStatement = Statement(
                user,
                "Parent statement",
                "...",
                QuestionType.ExclusiveChoice
        )

        val childStatement = Statement(
                owner = user,
                title = "Child statement",
                content = "...",
                questionType = QuestionType.OpenEnded,
                parentStatement = parentStatement
        )

        statementRepository.save(parentStatement)
        statementRepository.saveAndFlush(childStatement)

        // Expect:
        entityManager.refresh(childStatement)

        assertThat(parentStatement.id, notNullValue())
        assertThat(childStatement.id, notNullValue())
        assertThat(childStatement.parentStatement?.id, equalTo(parentStatement.id))
    }

    @Test
    fun `test questionSpecification serialization-deserialization - adhoc data`() {
        // Given: an adhoc choice specification to be serialized into the datasource
        val user = testingService.getAnyUser()
        val choiceSpecification : ChoiceSpecification = MultipleChoiceSpecification(
                nbCandidateItem = 3,
                expectedChoiceList = listOf(
                        ChoiceItem(1, 50f),
                        ChoiceItem(3, 50f)
                ),
                explanationChoiceList = listOf(
                        TeacherExplanation(1, "Un"),
                        TeacherExplanation(2, "Deux")
                )
        )


        val statement =
                statementRepository.saveAndFlush(
                        Statement(
                                owner = user,
                                title = "title",
                                content = "content",
                                questionType = QuestionType.MultipleChoice,
                                choiceSpecification = choiceSpecification
                        )
                )
        entityManager.refresh(statement)

        // Expect: the choice specification deserialized from the datasource to be
        // identical to the provided one
        assertThat(
                statement.choiceSpecification,
                equalTo(choiceSpecification)
        )
    }

    @Test
    fun `test questionSpecification serialization-deserialization - test data`() {
        // Given :
        val statement618 = statementRepository.findById(618).get()

        // Expect :
        assertThat(
                statement618.choiceSpecification,
                equalTo(
                        MultipleChoiceSpecification(
                                nbCandidateItem = 6,
                                expectedChoiceList = listOf(
                                        ChoiceItem(
                                                index = 2,
                                                score = 100f / 3f
                                        ),
                                        ChoiceItem(
                                                index = 3,
                                                score = 100f / 3f
                                        ),
                                        ChoiceItem(
                                                index = 6,
                                                score = 100f / 3f
                                        )
                                )
                        ) as ChoiceSpecification
                )
        )
    }
}
