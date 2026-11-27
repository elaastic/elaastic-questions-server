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

package org.elaastic.bootstrap.demo

import java.util.logging.Logger
import javax.transaction.Transactional
import org.elaastic.material.instructional.course.Course
import org.elaastic.material.instructional.course.CourseService
import org.elaastic.material.instructional.question.attachment.Attachment
import org.elaastic.material.instructional.question.attachment.AttachmentService
import org.elaastic.material.instructional.question.attachment.MimeType
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.material.instructional.statement.StatementService
import org.elaastic.material.instructional.subject.Subject
import org.elaastic.material.instructional.subject.SubjectService
import org.elaastic.user.UserService
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils

@Service
class DemoSeederService(
    private val userService: UserService,
    private val subjectService: SubjectService,
    private val courseService: CourseService,
    private val statementService: StatementService,
    private val attachmentService: AttachmentService,
    private val simulation: DemoSessionSimulation
) {

    private val logger: Logger = Logger.getLogger(DemoSeederService::class.java.name)

    @Transactional
    fun seedDemoContent() {
        val demoOwner = userService.findByUsername(DEMO_OWNER_LOGIN)!!
        if (courseService.findAllByOwner(demoOwner).isNotEmpty()) {
            logger.info("Skipping demo seeding because user $DEMO_OWNER_LOGIN already has courses")
            return
        }

        val owners = demoCourses.map { it.ownerLogin }.distinct().associateWith { login ->
            if (login == DEMO_OWNER_LOGIN) demoOwner else userService.findByUsername(login)!!
        }
        val courses = demoCourses.associateWith { definition ->
            courseService.save(Course(title = definition.title, owner = owners.getValue(definition.ownerLogin))).also {
                logger.info("Seeded demo course '${it.title}' for user ${definition.ownerLogin}")
            }
        }
        val learners = simulation.loadLearners()
        val subjects = courses.flatMap { (definition, course) ->
            definition.subjects.map { it to course }
        }
        val (emptySubjects, populatedSubjects) = subjects.partition { (definition, _) -> definition.questions.isEmpty() }
        emptySubjects.forEach { (definition, course) -> createSubject(definition, course) }
        populatedSubjects.forEach { (definition, course) ->
            simulation.createAssignments(createSubject(definition, course), learners)
        }
    }

    private fun createSubject(definition: DemoSubject, course: Course): Subject {
        val subject = subjectService.save(Subject(title = definition.title, owner = course.owner, course = course))
        val statements = definition.questions.map { question ->
            statementService.save(
                Statement(
                    owner = subject.owner,
                    title = question.title,
                    content = question.content,
                    questionType = question.questionType,
                    choiceSpecification = question.choiceSpecification,
                    expectedExplanation = question.expectedExplanation
                )
            )
        }
        statements.forEach { subject.addStatement(it) }
        definition.questions.zip(statements).forEach { (question, statement) ->
            question.attachment?.let { path -> attachImage(statement, path) }
        }
        return subject
    }

    private fun attachImage(statement: Statement, path: String) {
        val file = ResourceUtils.getFile("classpath:$path")
        val attachment = Attachment(
            name = "fileToAttached",
            originalFileName = file.name,
            size = file.length(),
            mimeType = MimeType("image/png")
        )
        file.inputStream().use { attachmentService.saveStatementAttachment(statement, attachment, it) }
    }
}
