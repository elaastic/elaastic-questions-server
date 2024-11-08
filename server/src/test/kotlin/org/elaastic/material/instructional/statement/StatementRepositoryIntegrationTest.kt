package org.elaastic.material.instructional.statement

import org.elaastic.material.instructional.question.ChoiceItem
import org.elaastic.material.instructional.question.ChoiceSpecification
import org.elaastic.material.instructional.question.MultipleChoiceSpecification
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.material.instructional.question.explanation.TeacherExplanation
import org.elaastic.questions.test.IntegrationTestingService
import org.exparity.hamcrest.date.DateMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class StatementRepositoryIntegrationTest(
    @Autowired val statementRepository: StatementRepository,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save and resave a valid statement`() {
        // Given :
        val title = "title"
        val content = "content"
        val user = integrationTestingService.getAnyUser()

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
        MatcherAssert.assertThat(statement.id, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(statement.version, CoreMatchers.equalTo(0L))
        MatcherAssert.assertThat(statement.dateCreated, DateMatchers.sameOrAfter(justBefore))
        MatcherAssert.assertThat(statement.dateCreated, DateMatchers.sameOrBefore(justAfter))


        // And when :
        statement.content = "updated"
        val statementV2 = statementRepository.saveAndFlush(
            statement
        )

        // Then :
        statementV2.let {
            MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(1L))
            Assertions.assertTrue(it.dateCreated < it.lastUpdated)
        }
    }

    @Test
    fun `read legacy data`() {
        MatcherAssert.assertThat(
            statementRepository.findAll().count(),
            CoreMatchers.equalTo(2)
        )
    }

    @Test
    fun `create a statement with a parent statement`() {
        // Given:
        val user = integrationTestingService.getAnyUser()

        val parentStatement = Statement(
            user,
            "Parent statement",
            "...",
            questionType = QuestionType.ExclusiveChoice
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

        MatcherAssert.assertThat(parentStatement.id, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(childStatement.id, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(childStatement.parentStatement?.id, CoreMatchers.equalTo(parentStatement.id))
    }

    @Test
    fun `test questionSpecification serialization-deserialization - adhoc data`() {
        // Given: an adhoc choice specification to be serialized into the datasource
        val user = integrationTestingService.getAnyUser()
        val choiceSpecification: ChoiceSpecification = MultipleChoiceSpecification(
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
        MatcherAssert.assertThat(
            statement.choiceSpecification,
            CoreMatchers.equalTo(choiceSpecification)
        )
    }

    @Test
    fun `test questionSpecification serialization-deserialization - test data`() {
        // Given :
        val statement618 = statementRepository.findById(618).get()

        // Expect :
        MatcherAssert.assertThat(
            statement618.choiceSpecification,
            CoreMatchers.equalTo(
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