package org.elaastic.questions.assignment

import com.nhaarman.mockitokotlin2.*
import org.elaastic.questions.directory.User
import org.elaastic.questions.security.TestSecurityConfig
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
    fun `test index - with  results`() {
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

    @Test
    fun `test save - valid`() {
        val assignmentId = 123L
        val title = "A title"

        val assignmentData = AssignmentController.AssignmentData(
                id = assignmentId,
                title = title,
                owner = user
        )

        mockMvc.perform(
                post("/assignment/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("assignmentData", assignmentData)
        )
                .andExpect(status().isFound())
                .andExpect(
                        redirectedUrlTemplate(
                                "/assignment/{assignmentId}",
                                assignmentId
                        )
                )
    }

    @Test
    fun `test save - invalid because of blank title`() {
        mockMvc.perform(
                post("/assignment/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "")
        )
                .andExpect(status().isBadRequest()) // no redirect, the page is re-rendered with error messages
    }

    // TODO test edit action

    // TODO test update action

    // TODO test delete action
}
