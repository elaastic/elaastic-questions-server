package org.elaastic.questions.api.practice.subject

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import com.toedter.spring.hateoas.jsonapi.MediaTypes
import org.elaastic.questions.api.practice.subject.question.*
import org.elaastic.questions.api.practice.subject.question.attachment.PracticeAttachment
import org.elaastic.questions.api.practice.subject.question.specification.ExclusiveChoiceQuestionSpecification
import org.elaastic.questions.api.practice.subject.question.specification.MultipleChoiceQuestionSpecification
import org.elaastic.questions.api.practice.subject.question.specification.OpenQuestionSpecification
import org.elaastic.questions.attachment.Attachment
import org.elaastic.questions.attachment.AttachmentService
import org.elaastic.questions.security.TestSecurityConfig
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import javax.persistence.EntityNotFoundException

@ExtendWith(SpringExtension::class)
@WebMvcTest(RestPracticeSubjectController::class)
@ComponentScan("com.toedter.spring.hateoas.jsonapi")
@ContextConfiguration(classes = [TestSecurityConfig::class])
@WithUserDetails("restClient")
internal class RestPracticeSubjectControllerTest(
    @Autowired val mockMvc: MockMvc,
) {

    @MockBean
    lateinit var practiceSubjectService: PracticeSubjectService

    @MockBean
    lateinit var attachmentService: AttachmentService

    @Test
    fun `test get subjects - with no 'since' date provided`() {
        mockMvc.get("/api/practice/v1/subjects")
            .andExpect {
                status {
                    isBadRequest()
                    reason("Required request parameter 'since' for method parameter type String is not present")
                }
            }
    }

    @Test
    fun `test get subjects - with invalid 'since' date provided`() {
        mockMvc.get("/api/practice/v1/subjects") {
            param("since", "Invalid date")
        }
            .andExpect {
                status {
                    isBadRequest()
                    reason("'since' parameter must be an ISO_DATE_TIME ; provided : 'Invalid date'")
                }
            }
    }

    @Test
    fun `test get subjects - nominal case`() {
        val subject1Title = "1st subject"
        val subject1Id = 1L
        whenever(practiceSubjectService.findAllPracticeSubject(any())).thenReturn(
            listOf(
                SummaryPracticeSubject(id = subject1Id, title = subject1Title),
                SummaryPracticeSubject(id = 2L, title = "2nd subject")
            )
        )

        mockMvc.get("/api/practice/v1/subjects") {
            param("since", "2022-01-03T16:36:00Z")
            accept(MediaTypes.JSON_API)
        }
            .andExpect {
                status {
                    isOk()
                }
                content { contentType(MediaTypes.JSON_API) }
                content { jsonPath("$.data.length()") { value(2) } }
                content { jsonPath("$.data[0].id") { value(1) } }
                content { jsonPath("$.data[0].type") { value("practice-subject") } }
                content { jsonPath("$.data[0].attributes.title") { value(subject1Title) } }
                content { jsonPath("$.data[0].links.self") { value(containsString("/subjects/$subject1Id")) } }
            }
    }

    @Test
    fun `test get subject - invalid id`() {
        mockMvc.get("/api/practice/v1/subjects/invalid")
            .andExpect {
                status {
                    isBadRequest()
                }
            }
    }

    @Test
    fun `test get subject - id not found`() {
        whenever(practiceSubjectService.getPracticeSubject(-1L))
            .thenThrow(EntityNotFoundException())

        mockMvc.get("/api/practice/v1/subjects/-1") {
            accept(MediaTypes.JSON_API)
        }
            .andExpect {
                status {
                    isNotFound()
                }
            }
    }

    @Test
    fun `test get subject - nominal case`() {
        val subjectId = 123L
        val subjectTitle = "Subject title"

        val topicId = 134L
        val topicTitle = "Topic title"

        val questionOpen = PracticeQuestion(
            id = 88L,
            rank = 1,
            title = "questionOpen",
            content = "Question 1",
            expectedExplanation = "Expected explanation 1",
            specification = OpenQuestionSpecification(),
            attachment = PracticeAttachment(100L, "attachment"),
            explanations = listOf(
                PracticeLearnerExplanation(11L, "Explanation 1"),
                PracticeLearnerExplanation(12L, "Explanation 2"),
                PracticeLearnerExplanation(13L, "Explanation 3"),
            )
        )
        val questionExclusive = PracticeQuestion(
            id = 2L,
            rank = 2,
            title = "questionExclusive",
            content = "Question 2",
            expectedExplanation = "Expected explanation 2",
            specification = ExclusiveChoiceQuestionSpecification(
                nbCandidateItem = 4,
                expectedChoiceIndex = 3
            ),
            attachment = PracticeAttachment(101L, "attachment n2"),
            explanations = listOf(
                PracticeLearnerExplanation(21L, "Explanation 1"),
                PracticeLearnerExplanation(22L, "Explanation 2"),
                PracticeLearnerExplanation(23L, "Explanation 3"),
            )
        )
        val questionMultiple = PracticeQuestion(
            id = 3L,
            rank = 3,
            title = "MultipleExclusive",
            content = "Question 3",
            expectedExplanation = "Expected explanation 3",
            specification = MultipleChoiceQuestionSpecification(
                nbCandidateItem = 4,
                expectedChoiceIndexList = listOf(1, 4)
            ),
            attachment = PracticeAttachment(102L, "Attachment n3"),
            explanations = listOf(
                PracticeLearnerExplanation(31L, "Explanation 1"),
            )
        )
        val questionNoAttachment = PracticeQuestion(
            id = 4L,
            rank = 4,
            title = "No Attachment",
            content = "Question 4",
            expectedExplanation = "Expected explanation 4",
            specification = MultipleChoiceQuestionSpecification(
                nbCandidateItem = 4,
                expectedChoiceIndexList = listOf(1, 4)
            ),
            attachment = null,
            explanations = listOf(
                PracticeLearnerExplanation(41L, "Explanation 1"),
            )
        )

        val questions = listOf(
            questionOpen,
            questionExclusive,
            questionMultiple,
            questionNoAttachment
        )

        whenever(practiceSubjectService.getPracticeSubject(subjectId)).thenReturn(
            PracticeSubject(
                id = subjectId,
                title = subjectTitle,
                questions = questions,
                topic = PracticeTopic(id = topicId, title = topicTitle),
                learners = setOf()
            )
        )

        mockMvc.get("/api/practice/v1/subjects/$subjectId") {
            accept(MediaTypes.JSON_API)
        }
            .andExpect {
                status {
                    isOk()
                }
            }
            .andExpect {
                content { contentType(MediaTypes.JSON_API) }
                content { jsonPath("$.data.id") { value(subjectId) } }
                content { jsonPath("$.data.type") { value("practice-subject") } }
                content { jsonPath("$.data.attributes.title") { value(subjectTitle) } }
                content { jsonPath("$.data.relationships.topic.data.id") { value(topicId) } }
                content { jsonPath("$.data.relationships.topic.data.type") { value("practice-topic") } }
                content { jsonPath("$.data.relationships.questions.data.length()") { value(4) } }
                content { jsonPath("$.data.relationships.questions.data[0].id") { value(questionOpen.id) } }
                content { jsonPath("$.data.relationships.questions.data[0].type") { value("practice-question") } }

                content { jsonPath("$.included") { isArray() } }
                content {
                    jsonPath("$.included.length()") {
                        value(
                            1 + // topic
                                    questions.size +
                                    questions.sumOf { it.explanations.size } +
                                    questions.count { it.attachment != null }
                        )
                    }
                }
                content { jsonPath("$.included[0:].id") { value(hasItem("88")) } }
                content { jsonPath("$.included[?(@.id == '88' && @.type == 'practice-question')]") { exists() } }
                content { jsonPath("$.included[?(@.id == $topicId && @.type == 'practice-topic')]") { exists() } }
            }
    }

    @Test
    fun `getAttachmentBlob - when the attachment does not exist or is not bound to a question ready to practice`() {
        val subjectId = -1L
        val questionId = -1L
        val attachmentId = -1L


        whenever(practiceSubjectService.isAttachmentReadyToPractice(subjectId, questionId, attachmentId))
            .thenReturn(false)

        mockMvc.get("/api/practice/v1/subjects/$subjectId/questions/$questionId/attachment/$attachmentId/blob") {
            accept(MediaTypes.JSON_API)
        }
            .andExpect {
                status {
                    isBadRequest()
                }
            }
    }


    @Test
    fun `getAttachmentBlob - should return the expected Attachment blob`() {
        // Given
        val subjectId = 1L
        val questionId = 12L
        val attachmentId = 123L
        val attachment = Attachment(name = "Testing attachment", originalFileName = "original.test")
        val inputStream = ByteArrayInputStream(
            "Faked content".toByteArray(StandardCharsets.UTF_8)
        )
        val inputStreamResource = InputStreamResource(inputStream)
        val expectedResponseEntity = ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${attachment.originalFileName}\"")
            .body(inputStreamResource)
        val controller = RestPracticeSubjectController(practiceSubjectService, attachmentService)

        whenever(practiceSubjectService.isAttachmentReadyToPractice(subjectId, questionId, attachmentId)).thenReturn(true)
        whenever(attachmentService.getAttachmentById(attachmentId)).thenReturn(attachment)
        whenever(attachmentService.getInputStreamForAttachment(attachment)).thenReturn(inputStream)

        // Act
        val response = controller.getAttachmentBlob(subjectId, questionId,  attachmentId)

        // Expect
        Assertions.assertEquals(expectedResponseEntity, response)
    }
}
