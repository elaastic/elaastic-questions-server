package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.test.directive.*
import org.elaastic.questions.test.TestingService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManager
import javax.validation.ConstraintViolationException

/**
 * @author John Tranier
 */
@SpringBootTest
@Transactional
@EnableJpaAuditing
internal class FakeExplanationRepositoryIntegrationTest(
        @Autowired val fakeExplanationRepository: FakeExplanationRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid fake explanation - with corresponding item`() {
        // Given a fake explanation
        val author = testingService.getAnyUser()
        val statement = testingService.getAnyStatement()
        FakeExplanation(
                content = "My fake explanation",
                author = author,
                correspondingItem = 3,
                statement = statement
        )
                // When saving the fake explanation
                .tWhen {
                    fakeExplanationRepository.saveAndFlush(it)
                    entityManager.refresh(it)
                    it
                }
                // Then the fake explanation should to be properly saved
                .tThen {
                    assertThat(it.id, not(nullValue()))
                    assertThat(it.author, equalTo(author))
                    assertThat(it.statement, equalTo(statement))
                }

    }

    @Test
    fun `save a valid fake explanation - without corresponding item`() {
        // Expect a fake explanation without corresponding item to be saved without error
        FakeExplanation(
                content = "My fake explanation",
                author = testingService.getAnyUser(),
                statement = testingService.getAnyStatement()
        )
                .tWhen {
                    fakeExplanationRepository.saveAndFlush(it)
                }
                .tNoProblem()
    }

    @Test
    fun `the content must be not blank`() {
        // Expect an error to be thrown when trying to save a fake explanation with blank content
        FakeExplanation(
                content = "",
                author = testingService.getAnyUser(),
                statement = testingService.getAnyStatement()
        )
                .tThen {
                    Assertions.assertThrows(ConstraintViolationException::class.java) {
                        fakeExplanationRepository.saveAndFlush(it)
                    }

                }

    }
}