package org.elaastic.questions.subject

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.ChoiceSpecification
import org.elaastic.questions.attachment.Attachment
import org.elaastic.questions.attachment.MimeType
import java.io.File
import java.time.LocalDate
import java.util.*

open class ExportSubjectData(
    val globalId: String,
    val owner: ExportUser,
    val title: String,
    val dateCreated: Date,
    val lastUpdated: Date?,
    val statements: List<ExportStatement>
) {
    val export = ExportMetadata()

    constructor(subject: Subject) : this(
        title = subject.title,
        owner = ExportUser(
            subject.owner.username,
            subject.owner.firstName,
            subject.owner.lastName,
            subject.owner.email,
        ),
        globalId = subject.globalId,
        dateCreated = subject.dateCreated,
        lastUpdated = subject.lastUpdated,
        statements = subject.statements.map { statement ->
            val attachment = statement.attachment
            ExportStatement(
                statement.title,
                statement.content,
                statement.questionType,
                statement.choiceSpecification,
                statement.expectedExplanation,
                attachment = if(attachment != null) ExportAttachment(attachment) else null,
            )
        }
    )

    fun getAttachmentList() = statements.mapNotNull { it.attachment }

    open class ExportStatement(
        val title: String,
        val content: String,
        val questionType: QuestionType,
        val choiceSpecification: ChoiceSpecification?,
        val expectedExplanation: String?,
        val attachment: ExportAttachment?,
    )

    open class ExportUser(
        val username: String,
        val firstName: String,
        val lastName: String,
        val email: String?
    )

    open class ExportMetadata {
        val version: Int = 1
        val date: LocalDate = LocalDate.now()
    }

    open class ExportAttachment(
        val name: String,
        val originalFileName: String,
        val path: String,
        val mimeType: MimeType,
    ) {
        constructor(attachment: Attachment): this(
            attachment.name,
            attachment.originalFileName ?: error("originalFileName must be non-empty"),
            attachment.path ?: error("path must be non-empty"),
            attachment.mimeType ?: error("mimeType must be non-empty"),
        )

        var attachmentFile: File? = null
    }
}