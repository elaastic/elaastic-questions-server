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
package org.elaastic.questions.assignment.sequence

import com.nhaarman.mockitokotlin2.*
import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.sequence.eventLog.*
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.directory.Role
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Spy
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Profile
import org.springframework.test.util.ReflectionTestUtils
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
internal class EventLogServiceIntegrationTest(
    @Autowired val integrationTestingService: IntegrationTestingService
    ) {

    @SpyBean
    lateinit var spyEventLogRepository: EventLogRepository

    @Autowired
    lateinit var eventLogService: EventLogService

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(eventLogService, "eventLogRepository", spyEventLogRepository)
    }

    @Test
    fun `create adds a log with TEACHER role if the sequence OWNER IS the USER`() {

        val sequence = integrationTestingService.getAnySequence()
        val user = sequence.owner

        eventLogService.create(sequence, user, Action.START, ObjectOfAction.SEQUENCE)
        tWhen { eventLogService.create(sequence, user, Action.START, ObjectOfAction.SEQUENCE) }
            .tThen {
                MatcherAssert.assertThat(it.action, CoreMatchers.equalTo(Action.START))
                MatcherAssert.assertThat(it.user, CoreMatchers.equalTo(user))
                MatcherAssert.assertThat(it.role, CoreMatchers.equalTo(Role.RoleId.TEACHER))
                MatcherAssert.assertThat(it.obj, CoreMatchers.equalTo(ObjectOfAction.SEQUENCE))
                MatcherAssert.assertThat(it.sequence, CoreMatchers.equalTo(sequence))
                MatcherAssert.assertThat(it.userAgent, CoreMatchers.nullValue())
            }
    }

    @Test
    fun `create adds a log with STUDENT role if the sequence OWNER is NOT the USER`() {

        val sequence = integrationTestingService.getAnySequence()
        val learner = integrationTestingService.getTestStudent()

        eventLogService.create(sequence, learner, Action.START, ObjectOfAction.SEQUENCE)
        tWhen { eventLogService.create(sequence, learner, Action.START, ObjectOfAction.SEQUENCE) }
            .tThen {
                MatcherAssert.assertThat(it.action, CoreMatchers.equalTo(Action.START))
                MatcherAssert.assertThat(it.user, CoreMatchers.equalTo(learner))
                MatcherAssert.assertThat(it.role, CoreMatchers.equalTo(Role.RoleId.STUDENT))
                MatcherAssert.assertThat(it.obj, CoreMatchers.equalTo(ObjectOfAction.SEQUENCE))
                MatcherAssert.assertThat(it.sequence, CoreMatchers.equalTo(sequence))
                MatcherAssert.assertThat(it.userAgent, CoreMatchers.nullValue())
            }
    }

    @Test
    fun `create adds a log with a userAgent when it is provided`() {

        val sequence = integrationTestingService.getAnySequence()
        val learner = integrationTestingService.getTestStudent()
        val userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) "

        eventLogService.create(sequence, learner, Action.START, ObjectOfAction.SEQUENCE, userAgent)
        tWhen { eventLogService.create(sequence, learner, Action.START, ObjectOfAction.SEQUENCE, userAgent) }
            .tThen {
                MatcherAssert.assertThat(it.action, CoreMatchers.equalTo(Action.START))
                MatcherAssert.assertThat(it.user, CoreMatchers.equalTo(learner))
                MatcherAssert.assertThat(it.role, CoreMatchers.equalTo(Role.RoleId.STUDENT))
                MatcherAssert.assertThat(it.obj, CoreMatchers.equalTo(ObjectOfAction.SEQUENCE))
                MatcherAssert.assertThat(it.sequence, CoreMatchers.equalTo(sequence))
                MatcherAssert.assertThat(it.userAgent, CoreMatchers.equalTo(userAgent))
            }
    }

    @Test
    fun `if results are not published, no event log is made`(){
        val sequence = integrationTestingService.getAnySequence()
        val user = integrationTestingService.getTestStudent()
        val userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) "
        val learnerSequence = LearnerSequence(user, sequence)
        learnerSequence.sequence.resultsArePublished = false

        eventLogService.consultPlayer(sequence, user, learnerSequence, userAgent)
        verify(spyEventLogRepository, never()).save(any())
    }

    @Test
    fun `consultPlayer should create event log if conditions are met - Face to face`() {
        val sequence = integrationTestingService.getAnySequence()
        val user = integrationTestingService.getTestStudent()
        val userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) "
        val learnerSequence = LearnerSequence(user, sequence)

        learnerSequence.sequence.resultsArePublished = true
        learnerSequence.sequence.executionContext = ExecutionContext.FaceToFace

        eventLogService.consultPlayer(sequence, user, learnerSequence, userAgent)
        verify(spyEventLogRepository, times(1)).save(any())
    }

    @Test
    fun `consultPlayer should create event log if conditions are met - Blended`() {
        val sequence = integrationTestingService.getAnySequence()
        val user = integrationTestingService.getTestStudent()
        val userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) "
        val learnerSequence = LearnerSequence(user, sequence)
        val interaction = integrationTestingService.getAnyInteraction()
        interaction.interactionType = InteractionType.Read

        learnerSequence.sequence.resultsArePublished = true
        learnerSequence.sequence.executionContext = ExecutionContext.Blended
        learnerSequence.activeInteraction = interaction

        eventLogService.consultPlayer(sequence, user, learnerSequence, userAgent)
        verify(spyEventLogRepository, times(1)).save(any())
    }

    @Test
    fun `consultPlayer should not create event log if interaction type is not Read - Blended`() {
        val sequence = integrationTestingService.getAnySequence()
        val user = integrationTestingService.getTestStudent()
        val userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) "
        val learnerSequence = LearnerSequence(user, sequence)

        learnerSequence.sequence.resultsArePublished = true
        learnerSequence.sequence.executionContext = ExecutionContext.Blended
        learnerSequence.activeInteraction?.interactionType = InteractionType.ResponseSubmission

        eventLogService.consultPlayer(sequence, user, learnerSequence, userAgent)
        verify(spyEventLogRepository, never()).save(any())
    }

    @Test
    fun `consultPlayer should create event log if conditions are met - Distance`() {
        val sequence = integrationTestingService.getAnySequence()
        val user = integrationTestingService.getTestStudent()
        val userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) "
        val learnerSequence = LearnerSequence(user, sequence)
        val interaction = integrationTestingService.getAnyInteraction()
        interaction.interactionType = InteractionType.Read

        learnerSequence.sequence.resultsArePublished = true
        learnerSequence.sequence.executionContext = ExecutionContext.Distance
        learnerSequence.activeInteraction = interaction

        eventLogService.consultPlayer(sequence, user, learnerSequence, userAgent)
        verify(spyEventLogRepository, times(1)).save(any())
    }

    @Test
    fun `consultPlayer should not create event log if interaction type is not Read - Distance`() {
        val sequence = integrationTestingService.getAnySequence()
        val user = integrationTestingService.getTestStudent()
        val userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) "
        val learnerSequence = LearnerSequence(user, sequence)

        learnerSequence.sequence.resultsArePublished = true
        learnerSequence.sequence.executionContext = ExecutionContext.Distance
        learnerSequence.activeInteraction?.interactionType = InteractionType.Evaluation

        eventLogService.consultPlayer(sequence, user, learnerSequence, userAgent)
        verify(spyEventLogRepository, never()).save(any())
    }
}


