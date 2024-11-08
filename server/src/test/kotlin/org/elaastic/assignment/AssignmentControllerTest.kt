package org.elaastic.assignment

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.elaastic.common.web.MessageBuilder
import org.elaastic.questions.security.TestSecurityConfig
import org.elaastic.material.instructional.subject.SubjectService
import org.elaastic.user.OnboardingState
import org.elaastic.user.User
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

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
        user.onboardingState = OnboardingState(user)

        mockMvc.perform(
                MockMvcRequestBuilders.get("/assignment")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `test index - with results`() {
        val assignmentPages =
            PageImpl<Assignment>(
                listOf(mock<Assignment>(), mock<Assignment>()),
                PageRequest.of(0, 2), 4
            )

        whenever(assignmentService.findAllByOwner(user)).thenReturn(
                assignmentPages
        )

        mockMvc.perform(MockMvcRequestBuilders.get("/assignment").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }
}