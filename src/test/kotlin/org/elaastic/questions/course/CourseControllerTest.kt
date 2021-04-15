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

package org.elaastic.questions.course

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.security.TestSecurityConfig
import org.elaastic.questions.subject.SubjectService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@WebMvcTest(CourseController::class)
@ContextConfiguration(classes = [TestSecurityConfig::class])
@WithUserDetails("teacher")
internal class CourseControllerTest(
        @Autowired val mockMvc: MockMvc,
        @Autowired val userDetailsService: UserDetailsService
){
    @MockBean
    lateinit var courseService: CourseService

    @MockBean
    lateinit var subjectService: SubjectService

    @MockBean
    lateinit var messageBuilder: MessageBuilder

    val user = userDetailsService.loadUserByUsername("teacher") as User

    @Test
    fun `test index - with no results`() {
        val coursePages =
                PageImpl<Course>(listOf(), PageRequest.of(0, 10), 0)

        whenever(courseService.findAllByOwner(user)).thenReturn(
                coursePages
        )

        mockMvc.perform(
                MockMvcRequestBuilders.get("/course")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `test index - with  results`() {
        val coursePages =
                PageImpl<Course>(
                        listOf(mock<Course>(), mock<Course>()),
                        PageRequest.of(0, 2), 4)

        whenever(courseService.findAllByOwner(user)).thenReturn(
                coursePages
        )

        mockMvc.perform(MockMvcRequestBuilders.get("/course").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `test save - valid`() {
        val courseId = 301L
        val title = "A random Title"

        val CourseData = CourseController.CourseData(
                id = courseId,
                title = title,
                owner = user
        )

        mockMvc.perform(
                MockMvcRequestBuilders.post("/course/save")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("courseData", CourseData)
        )
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(
                        MockMvcResultMatchers.redirectedUrlTemplate(
                                "/course/{courseId}",
                                courseId
                        )
                )
    }

    @Test
    fun `test save - invalid because of blank title`() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/course/save")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // no redirect, the page is re-rendered with error messages
    }
}

