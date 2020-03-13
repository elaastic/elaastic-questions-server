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

package org.elaastic.questions.lti

import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class LtiConsumerRepositoryIntegrationTest(
        @Autowired val ltiConsumerRepository: LtiConsumerRepository,
        @Autowired val em: EntityManager,
        @Autowired val testingService: TestingService
) {

    @Test
    fun `test save of a valid lti consumer`() {
        // given a valid object
        LtiConsumer(consumerName = "Edx", secret = "secret pass", key = "abcd1234edx").let {
            it.enableFrom = Date()
            // when saving the object
            ltiConsumerRepository.saveAndFlush(it).let {
                // then id and version are initialized
                assertThat("id should not be null", it.key, equalTo("abcd1234edx"))
                assertThat("date created should be intialized", it.dateCreated, notNullValue())
                assertThat("date updated should be initialized", it.lastUpdated, notNullValue())
                assertThat("consumer should be enabled", it.isEnabled, equalTo(1))
            }
        }
    }

    @Test
    fun `test save of a non valid lti consumer`() {
        // given a non valid object
        LtiConsumer(consumerName = "Edx", secret = "secret pass", key = "").let {
            // expect an exception is thrown when saving the attachment
            assertThrows<ConstraintViolationException> { ltiConsumerRepository.saveAndFlush(it) }
        }

    }

    @Test
    fun `test fetch of a save lti consumer`() {
        // given a valid saved object
        LtiConsumer(consumerName = "Edx", secret = "secret pass", key = "abcd1234edx").let {
            it.enableFrom = Date()
            ltiConsumerRepository.saveAndFlush(it).let {
                // when refreshing the saved object
                em.refresh(it)
                // then it has the expected value properties
                assertThat("id is as expected", it.key, equalTo("abcd1234edx"))
                assertThat("consumer name is as expected", it.consumerName, equalTo("Edx"))
                assertThat("consumer enableFrom is as expected", it.enableFrom, notNullValue())
                assertThat("consumer is enabled as expected", it.isEnabled, equalTo(1))
            }
        }
    }

    @Test
    fun `test find by id`() {
        tGiven {
            // an lti consumer
            testingService.getAnyLtiConsumer()
        }.tWhen {
            // searching for lti consumer with a given valid key
            ltiConsumerRepository.findById(it.key)
        }.tThen {
            // the lti consumer corresponding with the key is found
            assertTrue(it.isPresent)
            assertThat(it.get().key, equalTo(testingService.getAnyLtiConsumer().key))
            assertThat(it.get().consumerName, equalTo(testingService.getAnyLtiConsumer().consumerName))
        }.tExpect {
            // searching a lti Consumer with bad key, an exception is thrown
            assertThrows<NoSuchElementException> {
                ltiConsumerRepository.findById("nokey").get()
            }
        }
    }


}
