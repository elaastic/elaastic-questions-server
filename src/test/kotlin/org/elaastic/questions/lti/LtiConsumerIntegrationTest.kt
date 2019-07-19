package org.elaastic.questions.lti

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

@SpringBootTest
@Transactional
@EnableJpaAuditing
internal class LtiConsumerIntegrationTest(@Autowired val ltiConsumerRepository: LtiConsumerRepository,
                                          @Autowired val em: EntityManager) {

    @Test
    fun `test save of a valid lti consumer`() {
        // given a valid object
        LtiConsumer(consumerName = "Moodle", secret = "secret pass").let {
            it.id = "abcd1234"
            it.enableFrom = Date()
            // when saving the object
            ltiConsumerRepository.saveAndFlush(it).let {
                // then id and version are initialized
                assertThat("id should not be null", it.id, equalTo("abcd1234"))
                assertThat("date created should be intialized", it.dateCreated, notNullValue())
                assertThat("date updated should be initialized", it.lastUpdated, notNullValue())
                assertThat("consumer should be enabled", it.isEnabled, equalTo(1))
            }
        }
    }

    @Test
    fun `test save of a non valid lti consumer`() {
        // given a non valid object
        LtiConsumer(consumerName = "Moodle", secret = "secret pass").let {
            it.id = ""
            // expect an exception is thrown when saving the attachment
            assertThrows<ConstraintViolationException> { ltiConsumerRepository.saveAndFlush(it) }
        }

    }

    @Test
    fun `test fetch of a save lti consumer`() {
        // given a valid saved object
        LtiConsumer(consumerName = "Moodle", secret = "secret pass").let {
            it.id = "abcd1234"
            it.enableFrom = Date()
            ltiConsumerRepository.saveAndFlush(it).let {
                // when refreshing the saved object
                em.refresh(it)
                // then it has the expected value properties
                assertThat("id is as expected", it.id, equalTo("abcd1234"))
                assertThat("consumer name is as expected", it.consumerName, equalTo("Moodle"))
                assertThat("consumer enableFrom is as expected", it.enableFrom, notNullValue())
                assertThat("consumer is enabled as expected", it.isEnabled, equalTo(1))
            }
        }
    }


}
