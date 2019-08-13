package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.elaastic.questions.security.TestSecurityConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ExtendWith(SpringExtension::class)
@WebMvcTest(AssignmentController::class)
@ContextConfiguration(classes = [TestSecurityConfig::class])
@WithUserDetails("teacher")
internal class AssignmentControllerTest(
        @Autowired val mockMvc: MockMvc,
        @Autowired val userDetailsService: UserDetailsService
) {
    @MockBean lateinit var assignmentService: AssignmentService

    val user = userDetailsService.loadUserByUsername("teacher") as User

    @Test
    fun `test index - with no results`() {
        val assignmentPages =
                PageImpl<Assignment>(listOf(), PageRequest.of(0, 10), 0)

        `when`(assignmentService.findAllByOwner(user)).thenReturn(
                assignmentPages
        )

        mockMvc.perform(get("/assignment"))
                .andExpect(status().isOk)
    }

    @Test
    fun `test index - with  results`() {
        val assignmentPages =
                PageImpl<Assignment>(listOf(
                        mock(Assignment::class.java),
                        mock(Assignment::class.java)
                ), PageRequest.of(0, 2), 4)

        `when`(assignmentService.findAllByOwner(user)).thenReturn(
                assignmentPages
        )

        mockMvc.perform(get("/assignment"))
                .andExpect(status().isOk)
    }
}
