/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.material.instructional.subject

import org.elaastic.material.instructional.question.ChoiceSpecification
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.material.instructional.statement.StatementService
import org.elaastic.material.instructional.question.explanation.FakeExplanation
import org.elaastic.material.instructional.question.attachment.Attachment
import org.elaastic.material.instructional.question.attachment.MimeType
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

    constructor(subject: Subject, statementService: StatementService) : this(
        title = subject.title,
        owner = ExportUser(
            subject.owner.username,
            subject.owner.firstName,
            subject.owner.lastName,
            subject.owner.email,
        ),
        globalId = subject.globalId.toString(),
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
                statementService.findAllFakeExplanationsForStatement(statement).map { ExportFakeExplanation(it) },
                attachment = if (attachment != null) ExportAttachment(attachment) else null,
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
        val fakeExplanationList: List<ExportFakeExplanation>,
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
        val mimeType: MimeType?,
    ) {
        constructor(attachment: Attachment) : this(
            attachment.name,
            attachment.originalFileName ?: error("originalFileName must be non-empty"),
            attachment.path ?: error("path must be non-empty"),
            attachment.mimeType,
        )

        var attachmentFile: File? = null
    }

    open class ExportFakeExplanation(
        val correspondingItem: Int?,
        val content: String
    ) {
        constructor(fakeExplanation: FakeExplanation) : this(
            fakeExplanation.correspondingItem,
            fakeExplanation.content,
        )
    }
}