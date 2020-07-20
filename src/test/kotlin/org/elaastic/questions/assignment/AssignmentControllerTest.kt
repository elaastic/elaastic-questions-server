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

package org.elaastic.questions.assignment

import com.nhaarman.mockitokotlin2.*
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.security.TestSecurityConfig
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf


@ExtendWith(SpringExtension::class)
@WebMvcTest(AssignmentController::class)
@ContextConfiguration(classes = [TestSecurityConfig::class])
@WithUserDetails("teacher")
internal class AssignmentControllerTest(
        @Autowired val mockMvc: MockMvc,
        @Autowired val userDetailsService: UserDetailsService
) {
    @MockBean
    lateinit var assignmentService: AssignmentService

    @MockBean
    lateinit var messageBuilder: MessageBuilder

    @MockBean
    lateinit var subjectService: SubjectService

    val user = userDetailsService.loadUserByUsername("teacher") as User

    @Test
    fun `test index - with no results`() {
        val assignmentPages =
                PageImpl<Assignment>(listOf(), PageRequest.of(0, 10), 0)

        whenever(assignmentService.findAllByOwner(user)).thenReturn(
                assignmentPages
        )

        mockMvc.perform(
                get("/assignment")
                        .with(csrf())
        )
                .andExpect(status().isOk)
    }

    @Test
    fun `test index - with results`() {
        val assignmentPages =
                PageImpl<Assignment>(
                        listOf(mock<Assignment>(), mock<Assignment>()),
                        PageRequest.of(0, 2), 4)

        whenever(assignmentService.findAllByOwner(user)).thenReturn(
                assignmentPages
        )

        mockMvc.perform(get("/assignment").with(csrf()))
                .andExpect(status().isOk)
    }
}
