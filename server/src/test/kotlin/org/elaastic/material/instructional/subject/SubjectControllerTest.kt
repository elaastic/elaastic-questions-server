package org.elaastic.material.instructional.subject

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.elaastic.assignment.AssignmentService
import org.elaastic.common.web.MessageBuilder
import org.elaastic.material.instructional.question.attachment.AttachmentService
import org.elaastic.material.instructional.course.CourseService
import org.elaastic.material.instructional.statement.StatementService
import org.elaastic.questions.security.TestSecurityConfig
import org.elaastic.user.OnboardingState
import org.elaastic.user.User
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
@WebMvcTest(SubjectController::class)
@ContextConfiguration(classes = [TestSecurityConfig::class])
@WithUserDetails("teacher")
internal class SubjectControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val userDetailsService: UserDetailsService
) {
    @MockBean
    lateinit var subjectService: SubjectService

    @MockBean
    lateinit var statementService: StatementService

    @MockBean
    lateinit var attachmentService: AttachmentService

    @MockBean
    lateinit var messageBuilder: MessageBuilder

    @MockBean
    lateinit var assignmentService: AssignmentService

    @MockBean
    lateinit var sharedSubjectService: SharedSubjectService

    @MockBean
    lateinit var courseService: CourseService

    @MockBean
    lateinit var subjectExporter: SubjectExporter

    val user = userDetailsService.loadUserByUsername("teacher") as User

    @Test
    fun `test index - with no results`() {
        val subjectPages =
            PageImpl<Subject>(listOf(), PageRequest.of(0, 10), 0)

        whenever(subjectService.findAllByOwner(user)).thenReturn(
                subjectPages
        )
        user.onboardingState = OnboardingState(user)

        mockMvc.perform(
                MockMvcRequestBuilders.get("/subject")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `test index - with  results`() {
        val subjectPages =
            PageImpl(
                listOf(mock(), mock<Subject>()),
                PageRequest.of(0, 2), 4
            )

        whenever(subjectService.findAllByOwner(user)).thenReturn(
                subjectPages
        )
        user.onboardingState = OnboardingState(user)

        mockMvc.perform(MockMvcRequestBuilders.get("/subject").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `test save - valid`() {
        val subjectId = 301L
        val title = "A random Title"

        val subjectData = SubjectController.SubjectData(
            id = subjectId,
            title = title,
            owner = user
        )

        mockMvc.perform(
                MockMvcRequestBuilders.post("/subject/save")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("subjectData", subjectData)
        )
                .andExpect(MockMvcResultMatchers.status().isFound)
                .andExpect(
                    MockMvcResultMatchers.redirectedUrlTemplate(
                        "/subject/{subjectId}?activeTab=questions",
                        subjectId
                    )
                )
    }

    @Test
    fun `test save - invalid because of blank title`() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/subject/save")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest) // no redirect, the page is re-rendered with error messages
    }
}