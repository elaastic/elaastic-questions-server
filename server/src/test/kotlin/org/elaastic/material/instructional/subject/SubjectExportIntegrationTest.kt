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

import org.elaastic.material.instructional.question.attachment.Attachment
import org.elaastic.material.instructional.question.attachment.AttachmentRepository
import org.elaastic.material.instructional.question.attachment.AttachmentService
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.test.IntegrationTestingService
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import java.io.File
import java.util.*
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
class SubjectExportIntegrationTest(
    @Autowired val subjectExporter: SubjectExporter,
    @Autowired val subjectService: SubjectService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val attachmentService: AttachmentService,
    @Autowired val attachmentRepository: AttachmentRepository,

    ) {

    @Test
    fun `export a subject to zip and then import it`() {
        val teacher = integrationTestingService.getTestTeacher()

        // Create a subject
        val subject = subjectService.save(
            Subject(
                title = "A subject",
                owner = teacher
            )
        )
        subjectService.addStatement(
            subject,
            Statement.createDefaultStatement(teacher)
                .title("Stmt n 1")
                .content("Content 1 - Avec des caract�res accentu�s")
                .expectedExplanation("Expected 1")
        )
        val statement2 = subjectService.addStatement(
            subject,
            Statement.createDefaultStatement(teacher)
                .title("Stmt n�2")
                .content("Content 2")
        )
        val attachmentContent = "Attachement".toByteArray()
        val attachment2 = Attachment(
            name = "MyAttach2",
            originalFileName = "originalName2",
            size = attachmentContent.size.toLong(),
            toDelete = true
        ).also {
            attachmentService.saveStatementAttachment(
                statement = statement2,
                attachment = it,
                inputStream = attachmentContent.inputStream()
            ).toDelete = true
            attachmentRepository.saveAndFlush(it)

        }
        attachmentService.addStatementToAttachment(statement2, attachment2)

        // Export the subject to ZIP
        val zip = File.createTempFile("elaastic-" + UUID.randomUUID().toString(), null)
        zip.deleteOnExit() // Cleanup after the test

        zip.outputStream().use { out ->
            subjectExporter.exportToZip(subject, "test.zip", out)
        }

        // Import the subject from ZIP
        val importedSubject = zip.inputStream().use { input ->
            subjectExporter.importFromZip(teacher, input)
        }

        // Check the imported subject
        MatcherAssert.assertThat(importedSubject.id, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(importedSubject.version, CoreMatchers.equalTo(0))
        MatcherAssert.assertThat(importedSubject.title, CoreMatchers.equalTo(subject.title))
        MatcherAssert.assertThat(importedSubject.owner.id, CoreMatchers.equalTo(teacher.id))
        MatcherAssert.assertThat(importedSubject.globalId, CoreMatchers.not(subject.globalId))
        MatcherAssert.assertThat(importedSubject.statements.size, CoreMatchers.equalTo(subject.statements.size))
        (0..1)
            .map { i -> Pair(importedSubject.statements[i], subject.statements[i]) }
            .forEach { (importedStatement, originalStatement) ->
                MatcherAssert.assertThat(importedStatement.title, CoreMatchers.equalTo(originalStatement.title))
                MatcherAssert.assertThat(importedStatement.content, CoreMatchers.equalTo(originalStatement.content))
                MatcherAssert.assertThat(
                    importedStatement.expectedExplanation,
                    CoreMatchers.equalTo(originalStatement.expectedExplanation)
                )
            }
        MatcherAssert.assertThat(
            importedSubject.statements[1].attachment?.id,
            CoreMatchers.notNullValue()
        )
        MatcherAssert.assertThat(
            importedSubject.statements[1].attachment?.id,
            CoreMatchers.not(subject.statements[1].attachment?.id)
        )
    }
}