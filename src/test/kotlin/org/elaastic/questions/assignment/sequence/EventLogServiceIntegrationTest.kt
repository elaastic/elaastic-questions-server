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
import org.elaastic.questions.assignment.sequence.eventLog.Action
import org.elaastic.questions.assignment.sequence.eventLog.EventLogService
import org.elaastic.questions.assignment.sequence.eventLog.ObjectOfAction
import org.elaastic.questions.directory.Role
import org.elaastic.questions.directory.User
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.security.core.userdetails.UserDetailsService
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
internal class EventLogServiceIntegrationTest(
    @Autowired val userDetailsService: UserDetailsService,
    @Autowired val eventLogService: EventLogService,
    @Autowired val integrationTestingService: IntegrationTestingService
    ) {

    val learner = userDetailsService.loadUserByUsername("jtra") as User

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
}


