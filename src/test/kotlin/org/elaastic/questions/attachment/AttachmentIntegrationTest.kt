package org.elaastic.questions.attachment

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.assignment.StatementRepository
import org.elaastic.questions.attachment.datastore.DataIdentifier
import org.elaastic.questions.attachment.datastore.FileDataStore
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

@SpringBootTest
@Transactional
internal class AttachmentIntegrationTest(
        @Autowired val attachmentRepository: AttachmentRepository,
        @Autowired val statementRepository: StatementRepository,
        @Autowired val testingService: TestingService,
        @Autowired val em: EntityManager,
        @Autowired val attachmentService: AttachmentService,
        @Autowired val dataStore: FileDataStore
) {

    @Test
    fun `test save of a valid attachment`() {
        // given a valid attachment
        val attachment = Attachment(
                name = "MyAttach",
                originalName = "originalName",
                size = 1024,
                mimeType = MimeType()
        )
        attachment.path = "/to/path"
        attachment.dimension = Dimension(width = 100, height = 100)
        // when saving the attachment
        attachmentRepository.save(attachment)
        // then id and version are initialized
        assertThat("id should not be null", attachment.id, notNullValue())
        assertThat("version should be initialised", attachment.version, equalTo(0L))
    }

    @Test
    fun `test save of a non valid attachment`() {
        // given a non valid attachment
        val attachment = Attachment(
                name = "MyAttach",
                originalName = "",
                size = 1024,
                mimeType = MimeType()
        )
        attachment.path = "/to/path"
        attachment.dimension = Dimension(width = 100, height = 100)
        // expect an exception is thrown when saving the attachment
        assertThrows<ConstraintViolationException> { attachmentRepository.save(attachment) }
    }

    @Test
    fun `test fetch of a save attachment`() {
        // given a valid saved attachment
        val attachment = Attachment(
                name = "MyAttach",
                originalName = "originalName",
                size = 1024,
                mimeType = MimeType(MimeType.MimeTypesOfDisplayableImage.png.label)
        )
        attachment.path = "/to/path"
        attachment.dimension = Dimension(width = 100, height = 150)
        attachmentRepository.saveAndFlush(attachment)
        // when refreshing the saved attachment
        em.refresh(attachment)
        // then it has the expected value properties
        assertThat("mime type is not as expected", attachment.mimeType?.label, equalTo(MimeType.MimeTypesOfDisplayableImage.png.label))
        assertThat("dimension width is not as expected", attachment.dimension?.width, equalTo(100))
        assertThat("dimension height is not as expected", attachment.dimension?.height, equalTo(150))

    }

    @Test
    fun `test save an attachment with statement`() {
        // given a statement
        val statement =
                statementRepository.saveAndFlush(
                        Statement(
                                owner = testingService.getAnyUser(),
                                title = "a title",
                                content = "a content",
                                questionType = QuestionType.OpenEnded
                        )
                )
        // and an attachment associated with the statement
        val attachment = Attachment(
                name = "MyAttach",
                originalName = "originalName",
                size = 1024,
                mimeType = MimeType(),
                toDelete = false
        )
        attachment.path = "/to/path"
        attachment.dimension = Dimension(width = 100, height = 100)

        // when saving the attachement
        attachmentService.addStatementToAttachment(statement, attachment)

        // then id and version are initialized
        assertThat("id should not be null", attachment.id, notNullValue())
        assertThat("version should be initialised", attachment.version, equalTo(0L))

        // and the attachment is not to delete
        assertFalse(attachment.toDelete)

        // when refresching attachement
        em.refresh(attachment)

        // then it has the expected statement attached
        assertThat("statement is not as expected", attachment.statement, equalTo(statement))

    }

    @Test
    fun testSaveStatementAttachment() {
        // given "a statement and an attachment") {
        val statement = testingService.getAnyStatement()
        val content = "Content".toByteArray()
        Attachment(
                name = "MyAttach",
                originalName = "originalName",
                size = content.size.toLong(),
                toDelete = true
        ).tWhen("saving the statement attachment") {
            attachmentService.saveStatementAttachment(
                    statement = statement,
                    attachment = it,
                    inputStream = content.inputStream()
            )
        }.tThen("the attachment is stored in database and linked to the statement") {
            assertThat(it.id, notNullValue())
            assertThat(it.statement, equalTo(statement))
            assertFalse(it.toDelete)
            assertThat(
                    dataStore.getRecord(DataIdentifier(it.path!!))!!.stream.readAllBytes(),
                    equalTo(content))
        }
    }

    @AfterEach
    fun removeDataStore() {
        File(dataStore.path).deleteRecursively()
    }
}

