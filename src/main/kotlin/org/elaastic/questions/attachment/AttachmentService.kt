package org.elaastic.questions.attachment

import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.attachment.datastore.FileDataStore
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.io.InputStream
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.MemoryCacheImageInputStream
import javax.transaction.Transactional

@Service
@Transactional
class AttachmentService(
        @Autowired val attachmentRepository: AttachmentRepository,
        @Autowired val dataStore: FileDataStore
) {

    /**
     * Add a statement to an attachment
     *
     * @param statement the statement
     * @param attachment the attachment
     * @return the modified attachment
     */
    fun addStatementToAttachment(statement: Statement, attachment: Attachment): Attachment {
        attachment.statement = statement
        attachment.toDelete = false
        return attachmentRepository.save(attachment)
    }

    /**
     * Save a statement attachment
     *
     * @param statement the concerning statement
     * @param attachment the attachment
     * @param inputStream the input stream corresponding to attachment file
     * @param maxSizeInMega the max authorized size for an atachment in Megabytes
     */
    fun saveStatementAttachment(statement: Statement, attachment: Attachment, inputStream: InputStream): Attachment {
        val dataRecord = dataStore.addRecord(inputStream)
        attachment.path = dataRecord.identifier.toString()
        if (attachment.isDisplayableImage()) {
            attachment.dimension = getDimensionFromInputStream(inputStream)
        }
        return addStatementToAttachment(statement, attachment)
    }

    /**
     * Detach an attachment
     *
     * @param myAttachement the attachment to detach
     * @return the detached attachment
     */
    fun detachAttachmentFromStatement(user: User, statement: Statement): Statement  {
        if (statement.owner != user) throw AccessDeniedException("You are not autorized to access to this sequence")
        statement.attachment?.let {
            statement.attachment = null
            it.statement = null
            it.toDelete = true
            attachmentRepository.save(it)
        }
        return statement
    }

    private fun getDimensionFromInputStream(inputStream: InputStream): Dimension? {
        var reader: ImageReader? = null
        try {
            val memInputStream = MemoryCacheImageInputStream(inputStream)
            val imageReaders = ImageIO.getImageReaders(memInputStream)
            if (imageReaders.hasNext()) {
                reader = imageReaders.next()
                reader.input = memInputStream
                return Dimension(
                        width = reader.getWidth(reader.minIndex),
                        height = reader.getHeight(reader.minIndex)
                )
            } else {
                 return null
            }
        } finally {
            reader?.dispose()
        }
    }
}
