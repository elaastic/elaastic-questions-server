package org.elaastic.questions.attachment

import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.attachment.datastore.DataIdentifier
import org.elaastic.questions.attachment.datastore.FileDataStore
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.logging.Logger
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import javax.imageio.stream.MemoryCacheImageInputStream
import javax.transaction.Transactional

@Service
@Transactional
class AttachmentService(
        @Autowired val attachmentRepository: AttachmentRepository,
        @Autowired val dataStore: FileDataStore
) {

    val logger: Logger = Logger.getLogger(AttachmentService::class.java.name)

    /**
     * Get attachment by id
     */
    fun getAttachmentById(id: Long): Attachment {
        return attachmentRepository.findById(id).get()
    }

    /**
     * Add a statement to an attachment
     *
     * @param statement the statement
     * @param attachment the attachment
     * @return the modified attachment
     */
    fun addStatementToAttachment(statement: Statement, attachment: Attachment): Attachment {
        attachment.statement = statement
        statement.attachment = attachment
        attachment.toDelete = false
        return attachmentRepository.save(attachment)
    }

    /**
     * Save a statement attachment
     *
     * @param statement the concerning statement
     * @param attachment the attachment
     * @param inputStream the input stream corresponding to attachment file
     */
    fun saveStatementAttachment(statement: Statement, attachment: Attachment, inputStream: InputStream): Attachment {
        inputStream.use { iis ->
            val dataRecord = dataStore.addRecord(iis)
            attachment.path = dataRecord.identifier.toString()
            if (attachment.isDisplayableImage()) {
                dataRecord.stream.use {
                    attachment.dimension = getDimensionFromInputStream(it)
                }
            }
        }
        return addStatementToAttachment(statement, attachment)
    }

    /**
     * Detach an attachment
     *
     * @param statement the statement
     * @return the statement
     */
    fun detachAttachmentFromStatement(user: User, statement: Statement): Statement {
        if (statement.owner != user) throw AccessDeniedException("You are not autorized to access to this sequence")
        statement.attachment?.let {
            statement.attachment = null
            it.statement = null
            it.toDelete = true
            attachmentRepository.saveAndFlush(it)
        }
        return statement
    }

    /**
     * Return input stream corresponding to the given Attachement
     *
     * @param attachment the attachement
     * @return the input stream
     */
    fun getInputStreamForAttachement(attachment: Attachment): InputStream {
        val dataRecord = dataStore.getRecord(DataIdentifier(attachment.path!!))
        return dataRecord!!.stream
    }

    internal fun getDimensionFromInputStream(inputStream: InputStream): Dimension? {
        ImageIO.createImageInputStream(inputStream).let { iis ->
            ImageIO.getImageReaders(iis).let {
                while (it.hasNext()) {
                    val reader = it.next()
                    try {
                        reader.input = iis
                        return Dimension(
                                width = reader.getWidth(reader.minIndex),
                                height = reader.getHeight(reader.minIndex)
                        )
                    } catch (e: Exception) {
                        logger.severe(e.toString())
                    } finally {
                        reader.dispose()
                    }
                }
                return null
            }
        }
    }
}
