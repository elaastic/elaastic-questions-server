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

package org.elaastic.material.instructional.attachment

import org.elaastic.assignment.AssignmentService
import org.elaastic.filestore.DataIdentifier
import org.elaastic.filestore.FileDataStore
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.material.instructional.statement.StatementRepository
import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.elaastic.sequence.LearnerSequenceRepository
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.ResourceUtils
import java.io.File
import javax.persistence.EntityManager
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class AttachmentIntegrationTest(
    @Autowired val attachmentRepository: AttachmentRepository,
    @Autowired val learnerSequenceRepository: LearnerSequenceRepository,
    @Autowired val assignmentService: AssignmentService,
    @Autowired val statementRepository: StatementRepository,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val em: EntityManager,
    @Autowired val attachmentService: AttachmentService,
    @Autowired val dataStore: FileDataStore
) {

    @Test
    fun `test save of a valid attachment`() {
        // given a valid attachment
        val attachment = Attachment(
            name = "MyAttach",
            originalFileName = "originalName",
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
            originalFileName = "",
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
            originalFileName = "originalName",
            size = 1024,
            mimeType = MimeTypesOfDisplayableImage.png.toMimeType()
        )
        attachment.path = "/to/path"
        attachment.dimension = Dimension(width = 100, height = 150)
        attachmentRepository.saveAndFlush(attachment)
        // when refreshing the saved attachment
        em.refresh(attachment)
        // then it has the expected value properties
        assertThat(
            "mime type is not as expected",
            attachment.mimeType,
            equalTo(MimeTypesOfDisplayableImage.png.toMimeType())
        )
        assertThat("dimension width is not as expected", attachment.dimension?.width, equalTo(100))
        assertThat("dimension height is not as expected", attachment.dimension?.height, equalTo(150))

    }

    @Test
    fun `test save an attachment with statement`() {
        // given a statement
        val statement =
            statementRepository.saveAndFlush(
                Statement(
                    owner = integrationTestingService.getAnyUser(),
                    title = "a title",
                    content = "a content",
                    questionType = QuestionType.OpenEnded
                )
            )
        // and an attachment associated with the statement
        val attachment = Attachment(
            name = "MyAttach",
            originalFileName = "originalName",
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
        val statement = integrationTestingService.getAnyStatement()
        val content = "Content".toByteArray()
        Attachment(
            name = "MyAttach",
            originalFileName = "originalName",
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
                equalTo(content)
            )
        }
    }

    @Test
    fun testDetachAttachment() {
        // given "a statement and an attachment") {
        val statement = integrationTestingService.getAnyStatement()
        val content = "Content".toByteArray()
        val attachment = Attachment(
            name = "MyAttach",
            originalFileName = "originalName",
            size = content.size.toLong(),
            toDelete = true
        )
        tGiven("saving the statement attachment") {
            attachmentService.saveStatementAttachment(
                statement = statement,
                attachment = attachment,
                inputStream = content.inputStream()
            )
        }.tWhen("refresh attachment and statement") {
            em.refresh(statement)
            em.refresh(it)
            statement
        }.tWhen("detach the attachment") {
            attachmentService.detachAttachmentFromStatement(it.owner, it)
        }.tThen {
            assertThat(attachment.statement, nullValue())
            assertThat(statement.attachment, nullValue())
            assertTrue(attachment.toDelete)
        }
    }

    @Test
    fun testRemoveSequenceWithAttachment() {
        // given an assignment and a sequence with statement with attachment
        val assignment = integrationTestingService.getAnyAssignment()
        val sequence = assignment.sequences[0]
        var statement = sequence.statement
        val content = "Content".toByteArray()
        var attachment = Attachment(
            name = "MyAttach",
            originalFileName = "originalName",
            size = content.size.toLong(),
            toDelete = true
        )
        tGiven("saving the statement attachment") {
            attachmentService.saveStatementAttachment(
                statement = statement,
                attachment = attachment,
                inputStream = content.inputStream()
            )
        }.tWhen("removing the sequence") {
            assignmentService.removeSequence(assignment.owner, assignment.sequences[0])
            statement = statementRepository.findById(statement.id!!).get()
            attachment = attachmentRepository.findById(attachment.id!!).get()
            attachment
        }.tThen("statement is still there with its attachment") {
            assertThat(attachment.statement, notNullValue())
            assertFalse(attachment.toDelete)
            sequence
        }.tThen("no more learner sequences") {
            assertTrue(learnerSequenceRepository.findAllBySequence(sequence).isEmpty())
        }
    }

    @Test
    fun testIsdisplayableImage() {
        // given "a statement and an attachment") {
        val statement = integrationTestingService.getAnyStatement()
        val file = ResourceUtils.getFile("classpath:exemple.png")
        Attachment(
            name = "MyAttach",
            originalFileName = "originalName",
            size = file.length(),
            toDelete = true,
            mimeType = MimeTypesOfDisplayableImage.png.toMimeType()
        ).also {
            attachmentService.saveStatementAttachment(
                statement = statement,
                attachment = it,
                inputStream = file.inputStream()
            )
            em.refresh(it)
        }.tThen("the attachment is processed as a displayable image") {
            assertTrue(it.isDisplayableImage())
            assertThat(it.dimension, notNullValue())
            val ais = attachmentService.getInputStreamForAttachment(it)
            assertThat(attachmentService.getDimensionFromInputStream(ais), equalTo(it.dimension))
        }
    }

    @Test
    fun testGetDimensionForDisplay() {
        tGiven("given a dimension") {
            Dimension(800, 600)
        }.tWhen("get dimension for display") {
            Attachment.getDimensionForDisplay(it, 600, 600)
        }.tThen {
            assertThat(it, equalTo(Dimension(600, 450)))
        }
    }

    @Test
    fun `test the delete of an attachment and file system`() {
        //given: "two attachments to the same image"
        val statement = integrationTestingService.getAnyStatement()
        val statement2 = integrationTestingService.getLastStatement()
        val content = "Content".toByteArray()
        val attachment1 = Attachment(
            name = "MyAttach",
            originalFileName = "originalName",
            size = content.size.toLong(),
            toDelete = true
        ).also {
            attachmentService.saveStatementAttachment(
                statement = statement,
                attachment = it,
                inputStream = content.inputStream()
            ).toDelete = true
            attachmentRepository.saveAndFlush(it)
        }
        val attachment2 = Attachment(
            name = "MyAttach2",
            originalFileName = "originalName2",
            size = content.size.toLong(),
            toDelete = true
        ).also {
            attachmentService.saveStatementAttachment(
                statement = statement2,
                attachment = it,
                inputStream = content.inputStream()
            ).toDelete = true
            attachmentRepository.saveAndFlush(it)
        }

        //when: "the garbage collector is running "
        attachmentService.deleteAttachmentAndFileInSystem()
        // then: "the two attachments are deleted and the image record in system too"
        assertThat(attachmentRepository.findById(attachment1.id!!).orElse(null), nullValue())
        assertThat(attachmentRepository.findById(attachment2.id!!).orElse(null), nullValue())
        assertFalse(dataStore.getFile(DataIdentifier(attachment1.path!!)).exists())

    }

    @Test
    fun `test the delete of an attachment without the delete of the file system`() {
        //given: "two attachments to the same image"

        val statement = integrationTestingService.getAnyStatement()
        val statement2 = integrationTestingService.getLastStatement()
        val attachment1 = Attachment(
            name = "MyAttach",
            originalFileName = "originalName",
            size = 8,
            toDelete = true
        ).also {
            attachmentService.saveStatementAttachment(
                statement = statement,
                attachment = it,
                inputStream = "content1".toByteArray().inputStream()
            )
            assertFalse(it.toDelete)
        }
        val attachment2 = Attachment(
            name = "MyAttach2",
            originalFileName = "originalName2",
            size = 8,
            toDelete = true
        ).also {
            attachmentService.saveStatementAttachment(
                statement = statement2,
                attachment = it,
                inputStream = "content2".toByteArray().inputStream()
            ).toDelete = true
            attachmentRepository.saveAndFlush(it)
        }

        //when: "the garbage collector is running "
        attachmentService.deleteAttachmentAndFileInSystem()
        // then: "the two attachments are deleted and the image record in system too"
        assertThat(attachmentRepository.findById(attachment1.id!!).orElse(null), notNullValue())
        assertThat(attachmentRepository.findById(attachment2.id!!).orElse(null), nullValue())
        assertTrue(dataStore.getFile(DataIdentifier(attachment1.path!!)).exists())

    }

    @Test
    fun testGarbageCollectionOnLargerPopulation() {
        // Given a list of attachments, some are to delete
        val attachments = statementRepository.findAll().mapIndexed { index, statement ->
            Attachment(
                name = "MyAttach${index}",
                originalFileName = "originalName${index}",
                size = 8,
                toDelete = true
            ).also {
                attachmentService.saveStatementAttachment(
                    statement = statement,
                    attachment = it,
                    inputStream = "content${index}".toByteArray().inputStream()
                )
                if (index % 2 == 0) {
                    it.toDelete = true
                    attachmentRepository.saveAndFlush(it)
                }
            }
        }
        //when: "the garbage collector is running "
        attachmentService.deleteAttachmentAndFileInSystem()
        // then deletions appear as expected
        attachments.forEachIndexed { index, attachment ->
            if (index % 2 == 0) {
                assertThat(attachmentRepository.findById(attachment.id!!).orElse(null), nullValue())
                assertFalse(dataStore.getFile(DataIdentifier(attachment.path!!)).exists())
            } else {
                assertThat(attachmentRepository.findById(attachment.id!!).orElse(null), notNullValue())
                assertTrue(dataStore.getFile(DataIdentifier(attachment.path!!)).exists())
            }
        }
    }

    @Test
    fun testDuplicateAttachment() {
        // given "a statement and an attachment") {
        val statement = integrationTestingService.getAnyStatement()
        val content = "Content".toByteArray()
        val attachment = Attachment(
            name = "MyAttach",
            originalFileName = "originalName",
            size = content.size.toLong(),
            toDelete = true
        )
        tGiven("saving the statement attachment") {
            attachmentService.saveStatementAttachment(
                statement = statement,
                attachment = attachment,
                inputStream = content.inputStream()
            )
        }.tWhen("duplicate the attachment") {
            attachmentService.duplicateAttachment(it)
        }.tThen {
            assertThat(it, not(equalTo(attachment)))
            assertThat(it.toDelete, equalTo(attachment.toDelete))
            assertThat(it.size, equalTo(attachment.size))
            assertThat(it.mimeType, equalTo(attachment.mimeType))
            assertThat(it.originalFileName, equalTo(attachment.originalFileName))
            assertThat(it.path, equalTo(attachment.path))
            assertThat(it.dimension, equalTo(attachment.dimension))
        }
    }


    @AfterEach
    fun removeDataStore() {
        File(dataStore.path).deleteRecursively()
        dataStore.initDataStore()
    }
}

