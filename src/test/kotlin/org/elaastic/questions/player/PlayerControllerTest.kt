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

package org.elaastic.questions.player

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.LearnerSequenceService
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.eventLog.EventLogService
import org.elaastic.questions.assignment.sequence.interaction.InteractionService
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluationService
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.AnonymousUserService
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.player.components.results.TeacherResultDashboardService
import org.elaastic.questions.player.phase.LearnerPhaseService
import org.elaastic.questions.security.TestSecurityConfig
import org.elaastic.questions.subject.*
import org.elaastic.questions.test.FunctionalTestingService
import org.elaastic.questions.test.IntegrationTestingService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.togglz.core.manager.FeatureManager

@ExtendWith(SpringExtension::class)
@WebMvcTest(PlayerController::class)
@ContextConfiguration(classes = [TestSecurityConfig::class])
@WithUserDetails("teacher")
internal class PlayerControllerTest(
        @Autowired val mockMvc:MockMvc,
        @Autowired val userDetailsService:UserDetailsService,
        @Autowired val functionalTestingService: FunctionalTestingService
) {
    @MockBean
    lateinit var assignmentService: AssignmentService

    @MockBean
    lateinit var sequenceService: SequenceService

    @MockBean
    lateinit var learnerSequenceService: LearnerSequenceService

    @MockBean
    lateinit var interactionService: InteractionService

    @MockBean
    lateinit var responseService: ResponseService

    @MockBean
    lateinit var messageBuilder: MessageBuilder

    @MockBean
    lateinit var anonymousUserService: AnonymousUserService

    @MockBean
    lateinit var learnerPhaseService: LearnerPhaseService

    @MockBean
    lateinit var userService: UserService

    @MockBean
    lateinit var featureManager: FeatureManager

    @MockBean
    lateinit var teacherResultDashboardService: TeacherResultDashboardService

    @MockBean
    lateinit var chatGptEvaluationService: ChatGptEvaluationService

    @MockBean
    lateinit var eventLogService: EventLogService

    @MockBean
    lateinit var beanIntegrationTestingService: IntegrationTestingService

    @MockBean
    lateinit var beanFunctionalTestingService: FunctionalTestingService

    val user = userDetailsService.loadUserByUsername("teacher") as org.elaastic.questions.directory.User

    @Test
    fun `consultResults is called whenever a student access to a face to face sequence that is published`() {

        /* TODO - fix this test
        val subject = Subject("Subject", user)
        val statement = Statement(user, "Statement", "Content", QuestionType.OpenEnded, subject = subject)
        subject.statements.add(statement)
        val assignment = Assignment("Assignment", user, subject = subject)
        subject.assignments.add(assignment)
        val learner = org.elaastic.questions.directory.User(
            firstName = "firstname",
            lastName ="lastName",
            username ="username",
            plainTextPassword = "password")
        val userAgent = "userAgent"
        functionalTestingService.randomlyPlayAllSequences(listOf(learner), assignment)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/assignment/"+assignment.id+"/play")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
        verify(eventLogService.consultResults(assignment.sequences.first(), learner, userAgent), times(1));
        */

    }

}

