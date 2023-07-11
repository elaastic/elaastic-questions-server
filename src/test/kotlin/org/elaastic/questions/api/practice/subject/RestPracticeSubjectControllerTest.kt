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
import java.util.UUID
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
        val subject1Id = UUID.randomUUID()
        val subject2Id = UUID.randomUUID()

        whenever(practiceSubjectService.findAllPracticeSubject(any())).thenReturn(
            listOf(
                SummaryPracticeSubject(id = subject1Id, title = subject1Title),
                SummaryPracticeSubject(id = subject2Id, title = "2nd subject")
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
                content { jsonPath("$.data[0].id") { value(subject1Id.toString()) } }
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
        val unexistingId = UUID.randomUUID()

        whenever(practiceSubjectService.getPracticeSubject(unexistingId))
            .thenThrow(EntityNotFoundException())

        mockMvc.get("/api/practice/v1/subjects/$unexistingId") {
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
        val subjectId = UUID.randomUUID()
        val subjectTitle = "Subject title"

        val topicId = UUID.randomUUID()
        val topicTitle = "Topic title"

        val question1Id = UUID.randomUUID()
        val questionOpen = PracticeQuestion(
            id = question1Id,
            rank = 1,
            title = "questionOpen",
            content = "Question 1",
            expectedExplanation = "Expected explanation 1",
            specification = OpenQuestionSpecification(),
            attachment = PracticeAttachment(UUID.randomUUID(), "attachment"),
            explanations = listOf(
                PracticeLearnerExplanation(UUID.randomUUID(), "Explanation 1"),
                PracticeLearnerExplanation(UUID.randomUUID(), "Explanation 2"),
                PracticeLearnerExplanation(UUID.randomUUID(), "Explanation 3"),
            )
        )
        val questionExclusive = PracticeQuestion(
            id = UUID.randomUUID(),
            rank = 2,
            title = "questionExclusive",
            content = "Question 2",
            expectedExplanation = "Expected explanation 2",
            specification = ExclusiveChoiceQuestionSpecification(
                nbCandidateItem = 4,
                expectedChoiceIndex = 3
            ),
            attachment = PracticeAttachment(UUID.randomUUID(), "attachment n2"),
            explanations = listOf(
                PracticeLearnerExplanation(UUID.randomUUID(), "Explanation 1"),
                PracticeLearnerExplanation(UUID.randomUUID(), "Explanation 2"),
                PracticeLearnerExplanation(UUID.randomUUID(), "Explanation 3"),
            )
        )
        val questionMultiple = PracticeQuestion(
            id = UUID.randomUUID(),
            rank = 3,
            title = "MultipleExclusive",
            content = "Question 3",
            expectedExplanation = "Expected explanation 3",
            specification = MultipleChoiceQuestionSpecification(
                nbCandidateItem = 4,
                expectedChoiceIndexList = listOf(1, 4)
            ),
            attachment = PracticeAttachment(UUID.randomUUID(), "Attachment n3"),
            explanations = listOf(
                PracticeLearnerExplanation(UUID.randomUUID(), "Explanation 1"),
            )
        )
        val questionNoAttachment = PracticeQuestion(
            id = UUID.randomUUID(),
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
                PracticeLearnerExplanation(UUID.randomUUID(), "Explanation 1"),
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
                content { jsonPath("$.data.id") { value(subjectId.toString()) } }
                content { jsonPath("$.data.type") { value("practice-subject") } }
                content { jsonPath("$.data.attributes.title") { value(subjectTitle) } }
                content { jsonPath("$.data.relationships.topic.data.id") { value(topicId.toString()) } }
                content { jsonPath("$.data.relationships.topic.data.type") { value("practice-topic") } }
                content { jsonPath("$.data.relationships.questions.data.length()") { value(4) } }
                content { jsonPath("$.data.relationships.questions.data[0].id") { value(questionOpen.id.toString()) } }
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
                content { jsonPath("$.included[0:].id") { value(hasItem(question1Id.toString())) } }
                content { jsonPath("$.included[?(@.id == '$question1Id' && @.type == 'practice-question')]") { exists() } }
                content { jsonPath("$.included[?(@.id == '$topicId' && @.type == 'practice-topic')]") { exists() } }
            }
    }

    @Test
    fun `getAttachmentBlob - when the attachment does not exist or is not bound to a question ready to practice`() {
        val subjectId = UUID.randomUUID()
        val questionId = UUID.randomUUID()
        val attachmentId = UUID.randomUUID()


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
        val subjectId = UUID.randomUUID()
        val questionId = UUID.randomUUID()
        val attachmentId = UUID.randomUUID()
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
        whenever(attachmentService.getAttachmentByUuid(attachmentId)).thenReturn(attachment)
        whenever(attachmentService.getInputStreamForAttachment(attachment)).thenReturn(inputStream)

        // Act
        val response = controller.getAttachmentBlob(subjectId, questionId,  attachmentId)

        // Expect
        Assertions.assertEquals(expectedResponseEntity, response)
    }
}
